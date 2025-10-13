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
package org.noear.snack4.json;

import org.noear.snack4.ONode;
import org.noear.snack4.Feature;
import org.noear.snack4.Options;
import org.noear.snack4.codec.util.IoUtil;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Json 读取器
 *
 * @author noear noear 2025/3/16 created
 * @since 4.0
 * */
public class JsonReader {
    public static ONode read(String json) throws IOException {
        return read(json, null);
    }

    public static ONode read(String json, Options opts) throws IOException {
        return new JsonReader(new StringReader(json), opts).read();
    }

    public static ONode read(Reader reader, Options opts) throws IOException {
        return new JsonReader(reader, opts).read();
    }

    /// ///////////////

    private final Options opts;
    private final ParserState state;

    private StringBuilder stringBuilder;

    private StringBuilder getStringBuilder() {
        if (stringBuilder == null) {
            stringBuilder = new StringBuilder(32);
        } else {
            stringBuilder.setLength(0);
        }
        return stringBuilder;
    }

    public JsonReader(Reader reader) {
        this(reader, null);
    }

    public JsonReader(Reader reader, Options opts) {
        this.state = new ParserState(reader);
        this.opts = opts == null ? Options.DEF_OPTIONS : opts;
    }

    public ONode read() throws IOException {
        try {
            state.fillBuffer();
            ONode node = parseValue();
            state.skipWhitespace();

            if (opts.hasFeature(Feature.Read_AllowComment)) {
                state.skipComments();
            }

            if (state.bufferPosition < state.bufferLimit) {
                throw state.error("Unexpected data after json root");
            }
            return node;
        } finally {
            state.reader.close();
        }
    }

    private ONode parseValue() throws IOException {
        state.skipWhitespace();

        if (opts.hasFeature(Feature.Read_AllowComment)) {
            state.skipComments();
        }

        char c = state.peekChar();

        if (c == '{') return parseObject();
        if (c == '[') return parseArray();
        if (c == '"' || (opts.hasFeature(Feature.Read_DisableSingleQuotes) == false && c == '\'')) {
            String str = parseString();

            if (opts.hasFeature(Feature.Read_UnwrapJsonString)) {
                if (str.length() > 1) {
                    char c1 = str.charAt(0);
                    char c2 = str.charAt(str.length() - 1);
                    if ((c1 == '{' && c2 == '}') || (c1 == '[' && c2 == ']')) {
                        return ONode.ofJson(str, opts);
                    }
                }
            }

            return new ONode(opts, str);
        }
        // 新增的 JavaScript Date 对象支持
        if (c == 'n' && state.peekChar(1) == 'e' && state.peekChar(2) == 'w') {
            return parseDate();
        }

        if (c == '-' || (c >= '0' && c <= '9')) return new ONode(opts, parseNumber());
        if (c == 't') return parseKeyword("true", true);
        if (c == 'f') return parseKeyword("false", false);
        if (c == 'n') return parseKeyword("null", null);
        if (c == 'N') return parseKeyword("NaN", null);
        if (c == 'u') return parseKeyword("undefined", null);
        throw state.error("Unexpected character: " + c);
    }

    /**
     * 解析 JavaScript Date 对象: new Date(long)
     *
     * @return ONode (Date)
     * @throws IOException
     */
    private ONode parseDate() throws IOException {
        // 期望 "new Date("
        state.expect('n');
        state.expect('e');
        state.expect('w');
        state.skipWhitespace(); // 允许 'new' 后有空格
        state.expect('D');
        state.expect('a');
        state.expect('t');
        state.expect('e');
        state.expect('(');
        state.skipWhitespace();

        // 🌟 优化点：使用 parseNumber() 解析时间戳
        Number number = parseNumber();

        // 确保数字是 Long 类型或可以安全转换为 Long
        long timestamp;
        if (number instanceof Long) {
            timestamp = (Long) number;
        } else {
            // 如果是 Double/BigDecimal，可能丢失精度，但此处需做出决定
            timestamp = number.longValue();
        }

        state.skipWhitespace();
        state.expect(')'); // 期望 ')'

        return new ONode(opts, new Date(timestamp));
    }

    private ONode parseObject() throws IOException {
        Map<String, ONode> map = new LinkedHashMap<>();
        state.expect('{');
        while (true) {
            state.skipWhitespace();
            if (state.peekChar() == '}') {
                state.bufferPosition++;
                break;
            }

            String key = parseKey();

            if (key.isEmpty() && opts.hasFeature(Feature.Read_AllowEmptyKeys) == false) {
                throw new JsonParseException("Empty key is not allowed");
            }

            state.skipWhitespace();
            state.expect(':');
            ONode value = parseValue();
            map.put(key, value);

            state.skipWhitespace();
            if (state.peekChar() == ',') {
                state.bufferPosition++;
                state.skipWhitespace();
                if (state.peekChar() == '}') throw state.error("Trailing comma in object");
            } else if (state.peekChar() == '}') {
                // Continue to closing
            } else {
                throw state.error("Expected ',' or '}'");
            }
        }
        return new ONode(opts, map);
    }

    private String parseKey() throws IOException {
        String key;
        if (opts.hasFeature(Feature.Read_DisableUnquotedKeys) == false) {
            char c = state.peekChar();
            if (c != '"' && c != '\'') {
                key = parseUnquotedString();
            } else {
                key = parseString();
            }
        } else {
            key = parseString();
        }

        // 如果启用了蛇形转驼峰特性，则进行转换
        if (opts.hasFeature(Feature.Read_ConvertSnakeToCamel)) {
            key = convertSnakeToCamel(key);
        }
        return key;
    }

    private String convertSnakeToCamel(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) {
            return snakeCase;
        }

        StringBuilder result = getStringBuilder();
        boolean toUpperCase = false;
        boolean firstChar = true;

        for (int i = 0; i < snakeCase.length(); i++) {
            char c = snakeCase.charAt(i);

            if (c == '_') {
                // 下划线标记下一个字符需要大写
                toUpperCase = true;
            } else {
                if (toUpperCase) {
                    result.append(Character.toUpperCase(c));
                    toUpperCase = false;
                } else {
                    // 第一个字符保持小写，其他字符保持原样
                    if (firstChar) {
                        result.append(Character.toLowerCase(c));
                    } else {
                        result.append(c);
                    }
                }
                firstChar = false;
            }
        }

        return result.toString();
    }

    private String parseUnquotedString() throws IOException {
        StringBuilder sb = getStringBuilder();
        while (true) {
            char c = state.peekChar();
            if (c == ':' || c == ',' || c == '}' || c == ']' || Character.isWhitespace(c)) {
                break;
            }
            sb.append(state.nextChar());
        }
        return sb.toString();
    }

    private ONode parseArray() throws IOException {
        List<ONode> list = new ArrayList<>();
        state.expect('[');
        while (true) {
            state.skipWhitespace();
            if (state.peekChar() == ']') {
                state.bufferPosition++;
                break;
            }

            list.add(parseValue());

            state.skipWhitespace();
            if (state.peekChar() == ',') {
                state.bufferPosition++;
                state.skipWhitespace();
                if (state.peekChar() == ']') throw state.error("Trailing comma in array");
            } else if (state.peekChar() == ']') {
                // Continue to closing
            } else {
                throw state.error("Expected ',' or ']'");
            }
        }
        return new ONode(opts, list);
    }

    private String parseString() throws IOException {
        char quoteChar = state.nextChar();
        if (quoteChar != '"' && !(opts.hasFeature(Feature.Read_DisableSingleQuotes) == false && quoteChar == '\'')) {
            throw state.error("Expected string to start with a quote");
        }

        StringBuilder sb = getStringBuilder();
        while (true) {
            char c = state.nextChar();
            if (c == quoteChar) break;

            if (c == '\\') {
                c = state.nextChar();
                switch (c) {
                    case '"':
                    case '\'':
                    case '\\':
                        sb.append(c);
                        break;
                    case '/':
                        sb.append('/');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u': {
                        int val = 0;
                        for (int i = 0; i < 4; i++) {
                            char hex_char = state.nextChar();
                            val <<= 4; // val = val * 16
                            if (hex_char >= '0' && hex_char <= '9') {
                                val += hex_char - '0';
                            } else if (hex_char >= 'a' && hex_char <= 'f') {
                                val += hex_char - 'a' + 10;
                            } else if (hex_char >= 'A' && hex_char <= 'F') {
                                val += hex_char - 'A' + 10;
                            } else {
                                throw state.error("Invalid Unicode escape");
                            }
                        }
                        sb.append((char) val);
                        break;
                    }
                    default: {
                        if (c >= '0' && c <= '7') {
                            sb.append(IoUtil.CHARS_MARK_REV[(int) c]);
                        } else if (opts.hasFeature(Feature.Read_AllowInvalidEscapeCharacter)) {
                            sb.append(c);
                        } else if (opts.hasFeature(Feature.Read_AllowBackslashEscapingAnyCharacter)) {
                            sb.append('\\').append(c);
                        } else {
                            //RFC 8259
                            throw state.error("Invalid escape character: \\" + c);
                        }
                    }
                }
            } else {
                if (c < 0x20) {
                    if (opts.hasFeature(Feature.Read_AllowUnescapedControlCharacters) == false) {
                        //RFC 8259
                        throw state.error("Unescaped control character: 0x" + Integer.toHexString(c));
                    }
                }
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private Number parseNumber() throws IOException {
        StringBuilder sb = getStringBuilder();
        char c = state.peekChar();

        // 处理负数
        if (c == '-') {
            sb.append(state.nextChar());
        }

        // 解析整数部分
        if (opts.hasFeature(Feature.Read_AllowZeroLeadingNumbers) == false) {
            if (state.peekChar() == '0') {
                sb.append(state.nextChar());
                if (isDigit(state.peekChar())) {
                    throw state.error("Leading zeros not allowed");
                }
            }
        }

        if (isDigit(state.peekChar())) {
            while (isDigit(state.peekChar())) {
                sb.append(state.nextChar());
            }
        } else if (sb.length() == 0) {
            throw state.error("Invalid number format");
        }

        // 解析小数部分
        if (state.peekChar() == '.') {
            sb.append(state.nextChar());
            if (!isDigit(state.peekChar())) {
                throw state.error("Invalid decimal format");
            }
            while (isDigit(state.peekChar())) {
                sb.append(state.nextChar());
            }
        }

        // 解析指数部分
        if (state.peekChar() == 'e' || state.peekChar() == 'E') {
            sb.append(state.nextChar());
            if (state.peekChar() == '+' || state.peekChar() == '-') {
                sb.append(state.nextChar());
            }
            if (!isDigit(state.peekChar())) {
                throw state.error("Invalid exponent format");
            }
            while (isDigit(state.peekChar())) {
                sb.append(state.nextChar());
            }
        }

        // 检查类型后缀 (L, F, D, M) - 但不添加到字符串中
        char postfix = 0;
        if (state.peekChar() == 'L' || state.peekChar() == 'F' || state.peekChar() == 'D' || state.peekChar() == 'M') {
            postfix = state.nextChar(); // 读取后缀但不添加到sb中
        }

        String numStr = sb.toString();
        try {
            // 根据后缀类型解析数字
            if (postfix == 'D') {
                return Double.parseDouble(numStr);
            } else if (postfix == 'F') {
                return Float.parseFloat(numStr);
            } else if (postfix == 'L') {
                return Long.parseLong(numStr);
            } else {
                if (numStr.indexOf('.') >= 0 || numStr.indexOf('e') >= 0 || numStr.indexOf('E') >= 0) {
                    if (numStr.length() > 19 || opts.hasFeature(Feature.Read_UseBigNumberMode)) {
                        return new BigDecimal(numStr);
                    } else {
                        return Double.parseDouble(numStr);
                    }
                } else {
                    if (numStr.length() > 19 || opts.hasFeature(Feature.Read_UseBigNumberMode)) {
                        return new BigInteger(numStr);
                    } else {
                        long longVal = Long.parseLong(numStr);
                        if (longVal <= Integer.MAX_VALUE && longVal >= Integer.MIN_VALUE) {
                            return (int) longVal;
                        }
                        return longVal;
                    }
                }
            }
        } catch (NumberFormatException e) {
            throw state.error("Invalid number: " + numStr);
        }
    }

    private ONode parseKeyword(String expect, Object value) throws IOException {
        for (int i = 0; i < expect.length(); i++) {
            char c = state.nextChar();
            if (c != expect.charAt(i)) {
                throw state.error("Unexpected keyword: expected '" + expect + "'");
            }
        }
        return new ONode(opts, value);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    static class ParserState {
        private static final int BUFFER_SIZE = 8192;
        private final Reader reader;
        private long line = 1;
        private long column = 0;

        private final char[] buffer = new char[BUFFER_SIZE];
        private int bufferPosition;
        private int bufferLimit;

        public ParserState(Reader reader) {
            this.reader = reader;
        }

        private char nextChar() throws IOException {
            if (bufferPosition >= bufferLimit && !fillBuffer()) {
                throw error("Unexpected end of input");
            }
            char c = buffer[bufferPosition++];

            // 集中处理行/列计数
            if (c == '\n') {
                line++;
                column = 0;
            } else if (c == '\r') {
                // 检查 Windows 换行符 \r\n
                if (peekChar() == '\n') {
                    bufferPosition++; // 跳过下一个 \n
                }
                line++;
                column = 0;
            } else {
                column++;
            }

            return c;
        }

        private char peekChar() throws IOException {
            return peekChar(0);
        }

        private char peekChar(int offset) throws IOException {
            if (bufferPosition + offset >= bufferLimit && !fillBuffer()) {
                return 0;
            }
            return (bufferPosition + offset < bufferLimit) ? buffer[bufferPosition + offset] : 0;
        }

        private boolean fillBuffer() throws IOException {
            if (bufferPosition < bufferLimit) return true;
            bufferLimit = reader.read(buffer);
            bufferPosition = 0;
            return bufferLimit > 0;
        }

        private void expect(char expected) throws IOException {
            char c = nextChar();
            if (c != expected) {
                throw error("Expected '" + expected + "' but found '" + c + "'");
            }
        }

        private JsonParseException error(String message) {
            return new JsonParseException(message + " at line " + line + " column " + column);
        }

        private void skipWhitespace() throws IOException {
            while (true) {
                if (bufferPosition >= bufferLimit && !fillBuffer()) {
                    return; // 文件结束
                }

                char c = buffer[bufferPosition];
                if ((c == ' ' || c == '\t' || c == '\n' || c == '\r')) {
                    nextChar(); // 使用 nextChar() 确保行/列计数正确
                } else {
                    break;
                }
            }
        }

        private void skipComments() throws IOException {
            char c = peekChar();
            if (c == '/') {
                bufferPosition++;
                char next = peekChar();
                if (next == '/') {
                    skipLineComment();
                } else if (next == '*') {
                    skipBlockComment();
                }
            }
        }

        private void skipLineComment() throws IOException {
            while (true) {
                if (bufferPosition >= bufferLimit && !fillBuffer()) break;
                char c = buffer[bufferPosition];
                if (c == '\n') {
                    line++;
                    column = 0;
                    bufferPosition++;
                    break;
                }
                bufferPosition++;
                column++;
            }
        }

        private void skipBlockComment() throws IOException {
            bufferPosition++; // 跳过起始的 '/'
            boolean closed = false;
            while (true) {
                if (bufferPosition >= bufferLimit && !fillBuffer()) {
                    break;
                }
                char c = buffer[bufferPosition++];
                if (c == '\n') {
                    line++;
                    column = 0;
                } else if (c == '\r') {
                    if (peekChar() == '\n') bufferPosition++;
                    line++;
                    column = 0;
                } else {
                    column++;
                }

                if (c == '*' && peekChar() == '/') {
                    bufferPosition++;
                    closed = true;
                    break;
                }
            }
            if (!closed) {
                throw error("Unclosed block comment");
            }
        }
    }
}