/*
 * Copyright 2005-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.snack4.yaml;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.SnackException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;

/**
 * YAML 读取器（严格支持 YAML 1.2.2 标准）
 *
 * @author noear 2025/3/16 created
 * @since 4.0
 */
public class YamlReader {
    public static ONode read(String yaml) throws IOException {
        return read(yaml, null);
    }

    public static ONode read(String yaml, Options opts) throws IOException {
        return new YamlReader(new StringReader(yaml), opts).read();
    }

    public static ONode read(Reader reader) throws IOException {
        return new YamlReader(reader, null).read();
    }

    public static ONode read(Reader reader, Options opts) throws IOException {
        return new YamlReader(reader, opts).read();
    }

    /// ///////////////

    private final Options opts;
    private final ParserState state;
    private final YamlContext context;

    private StringBuilder stringBuilder;

    private StringBuilder getStringBuilder() {
        if (stringBuilder == null) {
            stringBuilder = new StringBuilder(32);
        } else {
            stringBuilder.setLength(0);
        }
        return stringBuilder;
    }

    public YamlReader(Reader reader) {
        this(reader, null);
    }

    public YamlReader(Reader reader, Options opts) {
        Objects.requireNonNull(reader, "reader");

        this.state = new ParserState(reader);
        this.opts = opts == null ? Options.DEF_OPTIONS : opts;
        this.context = new YamlContext();
    }

    public ONode read() throws IOException {
        try {
            state.fillBuffer();
            ONode node = parseDocument();
            state.skipWhitespace();

            if (state.bufferPosition < state.bufferLimit) {
                throw state.error("Unexpected data after YAML document");
            }
            return node;
        } finally {
            state.reader.close();
        }
    }

    private ONode parseDocument() throws IOException {
        state.skipWhitespace();

        // 处理文档开始标记
        if (state.consume("---")) {
            state.skipWhitespace();
            if (state.isEol()) {
                state.skipToNextLine();
            }
        }

        ONode node = parseNode();

        // 处理文档结束标记
        if (state.consume("...")) {
            state.skipWhitespace();
        }

        return node;
    }

    private ONode parseNode() throws IOException {
        state.skipWhitespace();

        if (state.isEol()) {
            state.skipToNextLine();
            return parseNode();
        }

        char c = state.peekChar();

        // 检查锚点和别名
        if (c == '&') {
            return parseAnchor();
        } else if (c == '*') {
            return parseAlias();
        }

        // 检查标签
        if (c == '!') {
            return parseTaggedValue();
        }

        // 根据内容判断节点类型
        if (c == '[') {
            return parseFlowSequence();
        } else if (c == '{') {
            return parseFlowMapping();
        } else if (c == '|' || c == '>') {
            return parseBlockScalar();
        } else if (c == '?') {
            return parseComplexKey();
        } else {
            // 普通标量或映射
            return parseScalarOrMapping();
        }
    }

    private ONode parseAnchor() throws IOException {
        state.expect('&');
        String anchor = parseAnchorName();
        state.skipWhitespace();

        ONode node = parseNode();
        context.addAnchor(anchor, node);
        return node;
    }

    private ONode parseAlias() throws IOException {
        state.expect('*');
        String alias = parseAnchorName();

        ONode node = context.getAnchor(alias);
        if (node == null) {
            throw state.error("Undefined alias: " + alias);
        }
        return node;
    }

    private String parseAnchorName() throws IOException {
        StringBuilder sb = getStringBuilder();
        while (true) {
            char c = state.peekChar();
            if (Character.isWhitespace(c) || c == ',' || c == '}' || c == ']' ||
                    c == ':' || c == '?' || c == '[' || c == '{' || state.isEol()) {
                break;
            }
            sb.append(state.nextChar());
        }
        return sb.toString();
    }

    private ONode parseTaggedValue() throws IOException {
        state.expect('!');
        String tag = parseTag();
        state.skipWhitespace();

        ONode value = parseNode();
        // 这里可以根据标签进行特殊处理
        return applyTag(value, tag);
    }

    private String parseTag() throws IOException {
        if (state.peekChar() == '<') {
            // 具名标签: !<tag:example.com,2000:app/tag>
            state.expect('<');
            StringBuilder sb = getStringBuilder();
            while (state.peekChar() != '>') {
                sb.append(state.nextChar());
            }
            state.expect('>');
            return sb.toString();
        } else {
            // 简单标签: !tag
            StringBuilder sb = getStringBuilder();
            while (!Character.isWhitespace(state.peekChar()) &&
                    state.peekChar() != ',' && state.peekChar() != '}' &&
                    state.peekChar() != ']' && state.peekChar() != ':' &&
                    !state.isEol()) {
                sb.append(state.nextChar());
            }
            return sb.toString();
        }
    }

    private ONode applyTag(ONode node, String tag) {
        // 处理标准 YAML 标签
        switch (tag) {
            case "!!str":
                if (!node.isString()) {
                    node.setValue(node.getString());
                }
                break;
            case "!!int":
                if (node.isString()) {
                    try {
                        node.setValue(new BigInteger(node.getString()));
                    } catch (NumberFormatException e) {
                        throw new SnackException("Invalid integer: " + node.getString());
                    }
                }
                break;
            case "!!float":
                if (node.isString()) {
                    try {
                        node.setValue(new BigDecimal(node.getString()));
                    } catch (NumberFormatException e) {
                        throw new SnackException("Invalid float: " + node.getString());
                    }
                }
                break;
            case "!!bool":
                if (node.isString()) {
                    String val = node.getString().toLowerCase();
                    if ("true".equals(val) || "yes".equals(val) || "on".equals(val)) {
                        node.setValue(true);
                    } else if ("false".equals(val) || "no".equals(val) || "off".equals(val)) {
                        node.setValue(false);
                    } else {
                        throw new SnackException("Invalid boolean: " + node.getString());
                    }
                }
                break;
            case "!!null":
                if (node.isString() && ("null".equals(node.getString()) || "~".equals(node.getString()) || "".equals(node.getString()))) {
                    node.setValue(null);
                }
                break;
            // 可以添加更多标签处理...
        }
        return node;
    }

    private ONode parseFlowSequence() throws IOException {
        List<ONode> list = new ArrayList<>();
        state.expect('[');
        state.skipWhitespace();

        while (true) {
            if (state.peekChar() == ']') {
                state.bufferPosition++;
                break;
            }

            list.add(parseNode());

            state.skipWhitespace();
            if (state.peekChar() == ',') {
                state.bufferPosition++;
                state.skipWhitespace();
            } else if (state.peekChar() != ']') {
                throw state.error("Expected ',' or ']' in flow sequence");
            }
        }
        return new ONode(opts, list);
    }

    private ONode parseFlowMapping() throws IOException {
        Map<String, ONode> map = new LinkedHashMap<>();
        state.expect('{');
        state.skipWhitespace();

        while (true) {
            if (state.peekChar() == '}') {
                state.bufferPosition++;
                break;
            }

            String key = parseFlowKey();
            state.skipWhitespace();
            state.expect(':');
            state.skipWhitespace();
            ONode value = parseNode();
            map.put(key, value);

            state.skipWhitespace();
            if (state.peekChar() == ',') {
                state.bufferPosition++;
                state.skipWhitespace();
            } else if (state.peekChar() != '}') {
                throw state.error("Expected ',' or '}' in flow mapping");
            }
        }
        return new ONode(opts, map);
    }

    private String parseFlowKey() throws IOException {
        state.skipWhitespace();
        char c = state.peekChar();

        if (c == '"') {
            return parseDoubleQuotedString();
        } else if (c == '\'') {
            return parseSingleQuotedString();
        } else {
            return parsePlainScalar();
        }
    }

    private ONode parseComplexKey() throws IOException {
        state.expect('?');
        state.skipWhitespace();

        // 复杂键处理
        if (state.peekChar() == '\n' || state.peekChar() == '\r') {
            // 块风格的复杂键
            state.skipToNextLine();
            context.indentLevel++;
            ONode key = parseNode();
            context.indentLevel--;

            state.skipWhitespace();
            state.expect(':');
            state.skipWhitespace();

            ONode value = parseNode();

            Map<String, ONode> map = new LinkedHashMap<>();
            map.put(key.getString(), value);
            return new ONode(opts, map);
        } else {
            // 流风格的复杂键
            ONode key = parseNode();
            state.skipWhitespace();
            state.expect(':');
            state.skipWhitespace();
            ONode value = parseNode();

            Map<String, ONode> map = new LinkedHashMap<>();
            map.put(key.getString(), value);
            return new ONode(opts, map);
        }
    }

    private ONode parseScalarOrMapping() throws IOException {
        String scalar = parsePlainScalar();

        state.skipWhitespace();

        if (state.peekChar() == ':' && !state.isEol()) {
            // 这是一个映射键
            Map<String, ONode> map = new LinkedHashMap<>();
            state.expect(':');
            state.skipWhitespace();

            if (state.isEol()) {
                // 块风格映射值
                state.skipToNextLine();
                context.indentLevel++;
                ONode value = parseNode();
                context.indentLevel--;
                map.put(scalar, value);
            } else {
                // 流风格映射值
                ONode value = parseNode();
                map.put(scalar, value);
            }

            // 检查同一层级的其他键值对
            while (true) {
                state.skipWhitespace();
                if (state.isEol() || state.getCurrentIndent() < context.indentLevel) {
                    break;
                }

                String nextKey = parsePlainScalar();
                state.skipWhitespace();
                if (state.peekChar() == ':') {
                    state.expect(':');
                    state.skipWhitespace();

                    if (state.isEol()) {
                        state.skipToNextLine();
                        context.indentLevel++;
                        ONode nextValue = parseNode();
                        context.indentLevel--;
                        map.put(nextKey, nextValue);
                    } else {
                        ONode nextValue = parseNode();
                        map.put(nextKey, nextValue);
                    }
                } else {
                    // 不是键值对，回退
                    break;
                }
            }

            return new ONode(opts, map);
        } else {
            // 纯标量值
            return parseScalarValue(scalar);
        }
    }

    private ONode parseScalarValue(String scalar) {
        // 根据内容推断类型
        if (scalar.equals("null") || scalar.equals("Null") || scalar.equals("NULL") || scalar.equals("~")) {
            return new ONode(opts, null);
        } else if (scalar.equals("true") || scalar.equals("True") || scalar.equals("TRUE") ||
                scalar.equals("yes") || scalar.equals("Yes") || scalar.equals("YES") ||
                scalar.equals("on") || scalar.equals("On") || scalar.equals("ON")) {
            return new ONode(opts, true);
        } else if (scalar.equals("false") || scalar.equals("False") || scalar.equals("FALSE") ||
                scalar.equals("no") || scalar.equals("No") || scalar.equals("NO") ||
                scalar.equals("off") || scalar.equals("Off") || scalar.equals("OFF")) {
            return new ONode(opts, false);
        } else if (scalar.equals(".inf") || scalar.equals(".Inf") || scalar.equals(".INF") ||
                scalar.equals("+.inf") || scalar.equals("+.Inf") || scalar.equals("+.INF")) {
            return new ONode(opts, Double.POSITIVE_INFINITY);
        } else if (scalar.equals("-.inf") || scalar.equals("-.Inf") || scalar.equals("-.INF")) {
            return new ONode(opts, Double.NEGATIVE_INFINITY);
        } else if (scalar.equals(".nan") || scalar.equals(".NaN") || scalar.equals(".NAN")) {
            return new ONode(opts, Double.NaN);
        } else if (NUMBER_PATTERN.matcher(scalar).matches()) {
            try {
                if (scalar.contains(".") || scalar.contains("e") || scalar.contains("E")) {
                    // 浮点数
                    if (scalar.length() > 19) {
                        return new ONode(opts, new BigDecimal(scalar));
                    } else {
                        return new ONode(opts, Double.parseDouble(scalar));
                    }
                } else {
                    // 整数
                    if (scalar.length() > 19) {
                        return new ONode(opts, new BigInteger(scalar));
                    } else {
                        long longVal = Long.parseLong(scalar);
                        if (longVal <= Integer.MAX_VALUE && longVal >= Integer.MIN_VALUE) {
                            return new ONode(opts, (int) longVal);
                        }
                        return new ONode(opts, longVal);
                    }
                }
            } catch (NumberFormatException e) {
                // 如果解析失败，当作字符串处理
                return new ONode(opts, scalar);
            }
        } else {
            // 字符串
            return new ONode(opts, scalar);
        }
    }

    private ONode parseBlockScalar() throws IOException {
        char style = state.nextChar(); // '|' 或 '>'

        // 解析块标量头部的修饰符
        boolean folded = (style == '>');
        boolean strip = true;
        int indent = -1;

        while (true) {
            char c = state.peekChar();
            if (c == '+' || c == '-') {
                strip = (c == '-');
                state.nextChar();
            } else if (c >= '1' && c <= '9') {
                indent = c - '0';
                state.nextChar();
            } else {
                break;
            }
        }

        state.skipToNextLine();

        StringBuilder sb = getStringBuilder();
        int currentIndent = state.getCurrentIndent();

        if (indent == -1) {
            indent = currentIndent;
        }

        boolean firstLine = true;
        boolean inIndent = false;

        while (true) {
            state.skipWhitespace();
            if (state.isEof()) break;

            int lineIndent = state.getCurrentIndent();
            if (lineIndent < currentIndent) break;

            if (lineIndent >= indent) {
                if (inIndent && folded) {
                    sb.append(' ');
                }

                String line = state.readLine();
                if (lineIndent > indent) {
                    line = line.substring(indent);
                }

                if (firstLine) {
                    sb.append(line);
                    firstLine = false;
                } else {
                    if (folded) {
                        if (line.isEmpty()) {
                            sb.append('\n');
                        } else {
                            sb.append(' ').append(line);
                        }
                    } else {
                        sb.append('\n').append(line);
                    }
                }
                inIndent = false;
            } else {
                // 空行或注释
                state.skipToNextLine();
                if (folded) {
                    sb.append('\n');
                } else {
                    sb.append('\n');
                }
                inIndent = true;
            }
        }

        String result = sb.toString();
        if (strip) {
            result = stripTrailing(result);
        }

        return new ONode(opts, result);
    }

    private String parsePlainScalar() throws IOException {
        StringBuilder sb = getStringBuilder();
        boolean inQuotes = false;

        while (true) {
            if (state.bufferPosition >= state.bufferLimit && !state.fillBuffer()) {
                break;
            }

            char c = state.buffer[state.bufferPosition];

            if (Character.isWhitespace(c) || c == ':' || c == ',' || c == ']' || c == '}' ||
                    c == '[' || c == '{' || c == '?' || c == '&' || c == '*' || c == '!' ||
                    c == '#' || state.isEol()) {
                break;
            }

            if (c == '"' || c == '\'') {
                if (inQuotes) {
                    break;
                } else {
                    inQuotes = true;
                }
            }

            sb.append(state.nextChar());
        }

        return sb.toString();
    }

    private String parseDoubleQuotedString() throws IOException {
        state.expect('"');
        StringBuilder sb = getStringBuilder();

        while (true) {
            char c = state.nextChar();
            if (c == '"') {
                break;
            } else if (c == '\\') {
                c = state.nextChar();
                switch (c) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case 'u':
                        int val = 0;
                        for (int i = 0; i < 4; i++) {
                            char hexChar = state.nextChar();
                            val <<= 4;
                            if (hexChar >= '0' && hexChar <= '9') {
                                val += hexChar - '0';
                            } else if (hexChar >= 'a' && hexChar <= 'f') {
                                val += hexChar - 'a' + 10;
                            } else if (hexChar >= 'A' && hexChar <= 'F') {
                                val += hexChar - 'A' + 10;
                            } else {
                                throw state.error("Invalid Unicode escape");
                            }
                        }
                        sb.append((char) val);
                        break;
                    default:
                        sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private String parseSingleQuotedString() throws IOException {
        state.expect('\'');
        StringBuilder sb = getStringBuilder();

        while (true) {
            char c = state.nextChar();
            if (c == '\'') {
                if (state.peekChar() == '\'') {
                    // 转义的单引号
                    state.nextChar();
                    sb.append('\'');
                } else {
                    break;
                }
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    // 数字模式匹配
    private static final Pattern NUMBER_PATTERN = Pattern.compile(
            "^[-+]?(\\d+|\\d+\\.\\d*|\\.\\d+)([eE][-+]?\\d+)?$"
    );

    static class ParserState {
        private static final int BUFFER_SIZE = 8192;
        final Reader reader;
        long line = 1;
        long column = 0;

        final char[] buffer = new char[BUFFER_SIZE];
        int bufferPosition;
        int bufferLimit;

        public ParserState(Reader reader) {
            this.reader = reader;
        }

        char nextChar() throws IOException {
            if (bufferPosition >= bufferLimit && !fillBuffer()) {
                throw error("Unexpected end of input");
            }
            char c = buffer[bufferPosition++];

            if (c == '\n') {
                line++;
                column = 0;
            } else if (c == '\r') {
                if (peekChar() == '\n') {
                    bufferPosition++;
                }
                line++;
                column = 0;
            } else {
                column++;
            }

            return c;
        }

        char peekChar() throws IOException {
            return peekChar(0);
        }

        char peekChar(int offset) throws IOException {
            if (bufferPosition + offset >= bufferLimit && !fillBuffer()) {
                return 0;
            }
            return (bufferPosition + offset < bufferLimit) ? buffer[bufferPosition + offset] : 0;
        }

        boolean fillBuffer() throws IOException {
            if (bufferPosition < bufferLimit) return true;
            bufferLimit = reader.read(buffer);
            bufferPosition = 0;
            return bufferLimit > 0;
        }

        void expect(char expected) throws IOException {
            char c = nextChar();
            if (c != expected) {
                throw error("Expected '" + expected + "' but found '" + c + "'");
            }
        }

        boolean consume(String str) throws IOException {
            for (int i = 0; i < str.length(); i++) {
                if (peekChar(i) != str.charAt(i)) {
                    return false;
                }
            }
            for (int i = 0; i < str.length(); i++) {
                nextChar();
            }
            return true;
        }

        YamlParseException error(String message) {
            return new YamlParseException(message + " at line " + line + " column " + column);
        }

        void skipWhitespace() throws IOException {
            while (true) {
                if (bufferPosition >= bufferLimit && !fillBuffer()) {
                    return;
                }
                char c = buffer[bufferPosition];
                if (c == ' ' || c == '\t') {
                    nextChar();
                } else {
                    break;
                }
            }
        }

        void skipToNextLine() throws IOException {
            while (!isEol() && !isEof()) {
                nextChar();
            }
            if (isEol()) {
                nextChar(); // 消耗换行符
            }
        }

        boolean isEol() throws IOException {
            char c = peekChar();
            return c == '\n' || c == '\r';
        }

        boolean isEof() throws IOException {
            return bufferPosition >= bufferLimit && !fillBuffer();
        }

        int getCurrentIndent() throws IOException {
            int savedPosition = bufferPosition;
            int indent = 0;

            while (true) {
                if (bufferPosition >= bufferLimit && !fillBuffer()) {
                    break;
                }
                char c = buffer[bufferPosition];
                if (c == ' ') {
                    indent++;
                    bufferPosition++;
                } else if (c == '\t') {
                    indent += 8 - (indent % 8);
                    bufferPosition++;
                } else {
                    break;
                }
            }

            int result = indent;
            bufferPosition = savedPosition;
            return result;
        }

        String readLine() throws IOException {
            StringBuilder sb = new StringBuilder();
            while (!isEol() && !isEof()) {
                sb.append(nextChar());
            }
            if (isEol()) {
                nextChar(); // 消耗换行符
            }
            return sb.toString();
        }
    }

    static class YamlContext {
        private final Map<String, ONode> anchors = new HashMap<>();
        int indentLevel = 0;

        void addAnchor(String name, ONode node) {
            anchors.put(name, node);
        }

        ONode getAnchor(String name) {
            return anchors.get(name);
        }
    }

    /**
     * 去除字符串末尾的空白字符（Java 8 兼容的 stripTrailing 替代）
     */
    private static String stripTrailing(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        int length = str.length();
        int end = length;

        // 从字符串末尾向前查找第一个非空白字符的位置
        while (end > 0 && Character.isWhitespace(str.charAt(end - 1))) {
            end--;
        }

        return end == length ? str : str.substring(0, end);
    }
}