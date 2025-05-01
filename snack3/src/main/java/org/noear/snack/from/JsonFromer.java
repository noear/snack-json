package org.noear.snack.from;

import org.noear.snack.ONode;
import org.noear.snack.OValue;
import org.noear.snack.exception.SnackException;
import org.noear.snack.core.Context;
import org.noear.snack.core.Feature;
import org.noear.snack.core.exts.CharBuffer;
import org.noear.snack.core.exts.CharReader;
import org.noear.snack.core.exts.ThData;
import org.noear.snack.core.utils.IOUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Json 解析器（将 json string 转为 ONode）
 * key：支持双引号、单引号、无引号
 * str：支持双引号、单引号
 */
public class JsonFromer implements Fromer {
    private static final ThData<CharBuffer> tlBuilder = new ThData<>(() -> new CharBuffer());

    /**
     * 清空线程缓存
     */
    public static void clear() {
        tlBuilder.remove();
    }

    @Override
    public void handle(Context ctx) throws IOException {
        ctx.target = do_handle(ctx, (String) ctx.source);
    }

    private CharBuffer getBuffer(Context ctx) {
        CharBuffer sBuf = null;
        if (ctx.options.hasFeature(Feature.DisThreadLocal)) {
            sBuf = new CharBuffer();//
        } else {
            sBuf = tlBuilder.get(); //
            sBuf.setLength(0);
        }

        return sBuf;
    }

    private ONode do_handle(Context ctx, String text) throws IOException {
        if (text == null) {
            return new ONode(null, ctx.options);
        } else {
            text = text.trim();//去除两边的空隔
        }

        int len = text.length();
        ONode node;

        //完整的处理（支持像："xx",'xx',12,true,{...},[],null,undefined 等）
        //
        if (len == 0) {
            node = new ONode(null, ctx.options);
        } else {
            char prefix = text.charAt(0);
            char suffix = text.charAt(text.length() - 1);

            if ((prefix == '{' && suffix == '}')
                    || (prefix == '[' && suffix == ']')) {
                //object or array
                //
                CharBuffer sBuf = getBuffer(ctx);

                node = new ONode(null, ctx.options);
                analyse(ctx, new CharReader(text), sBuf, node);

            } else if (len >= 2 && (
                    (prefix == '"' && suffix == '"') ||
                            (prefix == '\'' && suffix == '\''))) {
                //string
                //
                CharBuffer sBuf = getBuffer(ctx);
                CharReader sReader = new CharReader(text);
                sReader.read();
                scanString(sReader, sBuf, prefix);
                node = analyse_val(ctx, sBuf.toString(), true, false);
            } else if (prefix != '<' && len < 40) {
                //null,num,bool,other
                //
                node = analyse_val(ctx, text, false, true);
            } else {
                //普通的字符串
                node = new ONode(null, ctx.options);
                node.val().setString(text);
            }
        }

        return node;
    }

    public void analyse(Context ctx, CharReader sr, CharBuffer sBuf, ONode p) throws IOException {
        String name = null;

        boolean read_space1 = false;

        // 读入字符
        while (sr.read()) {
            char c = sr.value();

            // 根据字符
            switch (c) {
                case '"':
                    if (sBuf.length() > 0) {
                        //发现 " 之前，不应该有内容
                        throw new SnackException("Json string format is invalid: " + ctx.source);
                    }

                    scanString(sr, sBuf, '"');
                    if (analyse_buf(ctx, p, name, sBuf)) {
                        name = null;
                    }
                    break;

                case '\'':
                    scanString(sr, sBuf, '\'');
                    if (analyse_buf(ctx, p, name, sBuf)) {
                        name = null;
                    }
                    break;

                case '{':
                    if (sr.last() == '{') {
                        throw new SnackException("Json string format is invalid: " + ctx.source);
                    }

                    if (p.isObject()) {
                        analyse(ctx, sr, sBuf, p.getNew(name).asObject());
                        name = null;
                    } else if (p.isArray()) {
                        analyse(ctx, sr, sBuf, p.addNew().asObject());
                    } else {
                        analyse(ctx, sr, sBuf, p.asObject());
                    }
                    break;

                case '[':
                    if (p.isObject()) {
                        analyse(ctx, sr, sBuf, p.getNew(name).asArray());
                        name = null;
                    } else if (p.isArray()) {
                        analyse(ctx, sr, sBuf, p.addNew().asArray());
                    } else {
                        analyse(ctx, sr, sBuf, p.asArray());
                    }
                    break;

                case ':':
                    // 新的键名
                    name = sBuf.toString();
                    sBuf.setLength(0);
                    break;

                case ',':
                    if (sBuf.length() > 0) {
                        if (analyse_buf(ctx, p, name, sBuf)) {
                            name = null;
                        }
                    }
                    break;

                case '}':
                    if (sBuf.length() > 0) {
                        analyse_buf(ctx, p, name, sBuf);//都返回了，不需要name=null了
                    }
                    return;

                case ']':
                    if (sBuf.length() > 0) {
                        analyse_buf(ctx, p, name, sBuf);//都返回了，不需要name=null了
                    }
                    return;

                default:
                    if (sBuf.length() == 0) { //支持：new Date(xxx) //当中有空隔
                        if (c > 32) {//无引号的，只添加可见字符(key,no string val)
                            sBuf.append(c);

                            if (c == 'n') { //如果是 n开头, 可以读一次空隔
                                read_space1 = true;
                            }
                        }
                    } else {
                        if (c > 32) {
                            sBuf.append(c);
                        } else if (c == 32) {
                            if (read_space1) {
                                read_space1 = false;

                                if (sBuf.isString == false) {
                                    sBuf.append(c);
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }

    private boolean analyse_buf(Context ctx, ONode p, String name, CharBuffer sBuf) {
        if (p.isObject()) {
            if (name != null) {
                p.setNode(name, analyse_val(ctx, sBuf));
                sBuf.setLength(0);
                return true;
            }
        } else if (p.isArray()) {
            p.addNode(analyse_val(ctx, sBuf));
            sBuf.setLength(0);
        }
        return false;
    }

    private void scanString(CharReader sr, CharBuffer sBuf, char quote) throws IOException {
        //没有包括引号，不需要删除动作
        sBuf.isString = true;

        while (sr.read()) {
            char c = sr.value();

            if (quote == c) {
                return;
            }

            if ('\\' == c) {
                c = sr.next();

                if ('t' == c || 'r' == c || 'n' == c || 'f' == c || 'b' == c || '"' == c || '\'' == c || '/' == c || '\\' == c || (c >= '0' && c <= '7')) {
                    sBuf.append(IOUtil.CHARS_MARK_REV[(int) c]);
                    continue;
                }

                if ('x' == c) {
                    //16进制的处理。
                    char x1 = sr.next();
                    char x2 = sr.next();

                    int val = IOUtil.DIGITS_MARK[x1] * 16 + IOUtil.DIGITS_MARK[x2];
                    sBuf.append((char) val);
                    continue;
                }

                if ('u' == c) {
                    int val = 0;
                    //unicode的处理。对于码值大于0xFFFF的字符，仅支持\ud83d\udc4c这样4字节表示方法，不支持\u1f44c这样的两个半字节表示法
                    c = sr.next();
                    val = ((val << 4) + IOUtil.DIGITS_MARK[c]); //Character.digit(c, 16)
                    c = sr.next();
                    val = ((val << 4) + IOUtil.DIGITS_MARK[c]);
                    c = sr.next();
                    val = ((val << 4) + IOUtil.DIGITS_MARK[c]);
                    c = sr.next();
                    val = ((val << 4) + IOUtil.DIGITS_MARK[c]);
                    sBuf.append((char) val);
                    continue;
                }

                sBuf.append('\\');
                sBuf.append(c);
            } else {
                sBuf.append(c);
            }
        }
    }

    private ONode analyse_val(Context ctx, CharBuffer sBuf) {
        if (sBuf.isString == false) {
            sBuf.trimLast();//去掉尾部的空格
        }
        return analyse_val(ctx, sBuf.toString(), sBuf.isString, false);
    }

    /**
     * @param isNoterr 不抛出异常
     */
    private ONode analyse_val(Context ctx, String sval, boolean isString, boolean isNoterr) {
        ONode orst = null;

        if (isString) {
            if (ctx.options.hasFeature(Feature.StringJsonToNode)) {
                if ((sval.startsWith("{") && sval.endsWith("}")) ||
                        (sval.startsWith("[") && sval.endsWith("]"))) {
                    orst = ONode.loadStr(sval, ctx.options);
                }
            }

            if (orst == null) {
                orst = new ONode(null, ctx.options);
                orst.val().setString(sval);
            }
        } else {
            orst = new ONode(null, ctx.options);
            OValue oval = orst.val();

            char c = sval.charAt(0);
            int len = sval.length();

            if (c == 't' && len == 4) { //true
                oval.setBool(true);
            } else if (c == 'f' && len == 5) { //false
                oval.setBool(false);
            } else if (c == 'n' && len == 4) { // null or new (new not sup)
                oval.setNull();
            } else if (c == 'n' && sval.indexOf('D') == 4) { //new Date(xxx)
                long ticks = Long.parseLong(sval.substring(9, sval.length() - 1));
                oval.setDate(new Date(ticks));
            } else if (c == 'N' && len == 3) { // NaN
                oval.setNull();
            } else if (c == 'u' && len == 9) { // undefined
                oval.setNull();
            } else if ((c >= '0' && c <= '9') || (c == '-')) { //number
                if (sval.length() > 16) { //超过16位长度；采用大数字处理
                    if (sval.indexOf('.') > 0) {
                        oval.setNumber(new BigDecimal(sval));
                    } else {
                        oval.setNumber(new BigInteger(sval));
                    }
                } else { //小于16位长度；采用常规数字处理
                    if (sval.indexOf('.') > 0 || sval.indexOf('E') > 0) {
                        if (ctx.options.hasFeature(Feature.StringDoubleToDecimal)) {
                            oval.setNumber(new BigDecimal(sval));
                        } else {
                            oval.setNumber(Double.parseDouble(sval));
                        }
                    } else {
                        Long sval2 = Long.parseLong(sval);
                        if (sval2 > Integer.MAX_VALUE || ctx.options.hasFeature(Feature.ParseIntegerUseLong)) {
                            oval.setNumber(sval2);
                        } else {
                            oval.setNumber(sval2.intValue());
                        }
                    }
                }
            } else { //other
                if (isNoterr) {
                    oval.setString(sval);
                } else {
                    throw new SnackException("Format error!");
                }
            }
        }

        return orst;
    }
}
