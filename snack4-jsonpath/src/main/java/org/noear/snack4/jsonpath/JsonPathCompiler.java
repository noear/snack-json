package org.noear.snack4.jsonpath;

import org.noear.snack4.jsonpath.exception.JsonPathException;
import org.noear.snack4.jsonpath.segment.*;

import java.util.ArrayList;
import java.util.List;

public class JsonPathCompiler {
    /*
     * 编译
     * */
    public static JsonPath compile(String path) {
        return new JsonPathCompiler(path).doCompile();
    }


    private final String path;
    private int index;
    private List<SegmentFunction> segments = new ArrayList<>();

    private JsonPathCompiler(String path) {
        this.path = path;
    }

    private JsonPath doCompile() {
        index = 0; // 起始位置为 $ 符号
        index++;
        Context context = new Context(null); //记录分析中的 flattened 变化

        while (index < path.length()) {
            skipWhitespace();
            if (index >= path.length()) break;

            char ch = path.charAt(index);
            if (ch == '.') {
                resolveDot(context);
            } else if (ch == '[') {
                resolveBracket(context);
            } else {
                throw new JsonPathException("Unexpected character '" + ch + "' at index " + index);
            }
        }

        return new JsonPath(path, segments);
    }

    /**
     * 分析 '.' 或 '..' 操作符
     *
     */
    private void resolveDot(Context context) {
        index++;
        if (index < path.length() && path.charAt(index) == '.') {
            index++;

            if (path.charAt(index) == '*') {
                index++;
            } else {
                context.flattened = true;
            }

            segments.add(new RecursiveSegment());

            while (index < path.length()) {
                skipWhitespace();
                if (index >= path.length()) break;
                char ch = path.charAt(index);
                if (ch == '.' || ch == '[') {
                    if (ch == '.') {
                        resolveDot(context);
                    } else if (ch == '[') {
                        resolveBracket(context);
                    }
                } else {
                    break;
                }
            }

            if (index < path.length() && path.charAt(index) != '.' && path.charAt(index) != '[') {
                resolveKey(true);
                context.flattened = false;
            }
        } else {
            char ch = path.charAt(index);
            if (ch == '[') {
                resolveBracket(context);
            } else {
                resolveKey(false);
            }
        }
    }

    /**
     * 分析 '[...]' 操作符
     *
     */
    private void resolveBracket(Context context) {
        index++; // 跳过'['
        String segment = parseSegment(']');
        while (index < path.length() && path.charAt(index) == ']') {
            index++;
        }

        if (segment.startsWith("$.") || segment.startsWith("@.")) {
            segments.add(new DynamicIndexSegment(segment));
            context.flattened = false;
            return;
        }

        if (segment.equals("*")) {
            // 全选
            segments.add(new WildcardSegment(false));
        } else {
            try {
                if (segment.startsWith("?")) {
                    // 条件过滤，如 [?@id]
                    // ..*[?...] 支持进一步深度展开
                    // ..x[?...] 已展开过，但查询后是新的结果可以再展开
                    // ..[?...] 已展开过，不需要再展开
                    segments.add(new FilterSegment(segment, context.flattened));
                } else if (segment.contains(",")) {
                    // 多索引选择，如 [1,4], ['a','b']
                    segments.add(new MultiIndexSegment(segment));
                } else if (segment.contains(":")) {
                    // 范围选择，如 [1:4]
                    segments.add(new RangeIndexSegment(segment));
                } else {
                    // 属性选择
                    segments.add(new IndexSegment(segment));
                }
            } finally {
                context.flattened = false;
            }
        }
    }

    /**
     * 分析键名或函数操作符（如 "store" 或 "count()"）
     *
     */
    private void resolveKey(boolean flattened) {
        String key = parseSegment('.', '[');

        if (key.endsWith("()")) {
            segments.add(new FunctionSegment(key));
        } else if (key.equals("*")) {
            segments.add(new WildcardSegment(flattened));
        } else {
            segments.add(new PropertySegment(key));
        }
    }


    // 解析路径段（支持终止符列表）
    // 在 JsonPathCompiler.java 中

    /**
     * 解析路径段（支持终止符列表），同时健壮地处理引号、正则和嵌套方括号。
     *
     * @param terminators 允许的终止字符，如 '.', '[' 或 ']'。
     * @return 解析到的路径段字符串。
     */
    private String parseSegment(char... terminators) {
        StringBuilder sb = new StringBuilder();
        boolean inQuote = false; // 是否在引号内部
        char quoteChar = 0;
        boolean inRegex = false; // 是否在正则表达式内部 (仅在过滤器中可能出现)

        // 检查当前解析是否是针对方括号内部的内容（即 resolveBracket 调用时）
        boolean parsingBracketContent = isTerminator(']', terminators);
        int bracketLevel = parsingBracketContent ? 1 : 0; // 如果解析方括号内容，初始级别为 1

        while (index < path.length()) {
            char ch = path.charAt(index);

            // 1. 处理引号内的内容
            if ((ch == '\'' || ch == '\"') && !inRegex) {
                if (inQuote && ch == quoteChar) {
                    // 引号结束
                    inQuote = false;
                    quoteChar = 0;
                } else if (!inQuote) {
                    // 引号开始
                    inQuote = true;
                    quoteChar = ch;
                }
                sb.append(ch);
                index++;
                continue;
            }

            // 如果在引号内部，则除了上面的引号关闭逻辑，其他所有字符都只追加
            if (inQuote) {
                sb.append(ch);
                index++;
                continue;
            }

            // 2. 处理正则表达式的开始/结束 (仅在过滤器中适用)
            if (ch == '/' && !inRegex) {
                inRegex = true;
                sb.append(ch);
                index++;
                continue;
            } else if (ch == '/' && inRegex) {
                inRegex = false;
                sb.append(ch);
                index++;
                continue;
            }

            // 如果在正则表达式内部，忽略终止符检查
            if (inRegex) {
                sb.append(ch);
                index++;
                continue;
            }


            // 3. 处理嵌套的方括号 (仅在解析方括号内容时激活)
            if (parsingBracketContent) {
                if (ch == '[') {
                    bracketLevel++;
                } else if (ch == ']') {
                    bracketLevel--;
                    if (bracketLevel == 0) {
                        index++; // 跳过闭合的 ]
                        break;
                    }
                }
            }

            // 4. 检查外部终止符（仅在非方括号内容解析模式下，或在方括号内容解析但遇到非方括号终止符时）
            if (!parsingBracketContent || bracketLevel == 1) {
                if (isTerminator(ch, terminators)) {
                    // 遇到非方括号的终止符 (如 . 或 [)
                    if (ch != ']') {
                        break;
                    }
                }
            }

            sb.append(ch);
            index++;
        }

        return sb.toString().trim();
    }

    private boolean isTerminator(char ch, char[] terminators) {
        for (char t : terminators) {
            if (ch == t) return true;
        }
        return false;
    }

    // 跳过空白字符
    private void skipWhitespace() {
        while (index < path.length() && Character.isWhitespace(path.charAt(index))) {
            index++;
        }
    }
}