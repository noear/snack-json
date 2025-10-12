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
package org.noear.snack4.jsonpath;

import org.noear.snack4.jsonpath.segment.*;

import java.util.ArrayList;
import java.util.List;

/**
 * JsonPath 编译器
 *
 * @author noear
 * @since 4.0
 * */
public class JsonPathCompiler {
    /*
     * 编译
     * */
    public static JsonPath compile(String path) {
        return new JsonPathCompiler(path).doCompile();
    }

    private final String path;
    private int position;
    private List<Segment> segments = new ArrayList<>();
    private Segment lastSegment = null;

    private JsonPathCompiler(String path) {
        this.path = path;
    }

    private void addSegment(AbstractSegment segment) {
        segment.before(lastSegment);
        lastSegment = segment;

        segments.add(segment);
    }

    private JsonPath doCompile() {
        position = 1; //Skip $, @
        QueryContext ctx = new QueryContext(null, QueryMode.SELECT); //记录分析中的 flattened 变化

        while (position < path.length()) {
            skipWhitespace();
            if (position >= path.length()) break;

            char ch = path.charAt(position);
            if (ch == '.') {
                resolveDot(ctx);
            } else if (ch == '[') {
                resolveBracket(ctx);
            } else {
                throw new JsonPathException("Unexpected character '" + ch + "' at index " + position);
            }
        }

        return new JsonPath(path, segments);
    }

    /**
     * 分析 '.' 或 '..' 操作符
     */
    private void resolveDot(QueryContext context) {
        position++;
        if (position < path.length() && path.charAt(position) == '.') {
            addSegment(new DescendantSegment());

            while (position < path.length()) {
                skipWhitespace();
                if (position >= path.length()) break;
                char ch = path.charAt(position);
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

            if (position < path.length() && path.charAt(position) != '.' && path.charAt(position) != '[') {
                resolveKey();
            }
        } else {
            char ch = path.charAt(position);
            if (ch == '[') {
                resolveBracket(context);
            } else {
                resolveKey();
            }
        }
    }

    /**
     * 分析 '[...]' 操作符
     */
    private void resolveBracket(QueryContext context) {
        position++; // 跳过'['
        String segment = parseSegment(']');
        while (position < path.length() && path.charAt(position) == ']') {
            position++;
        }

        addSegment(new SelectSegment(segment));
    }

    /**
     * 分析键名或函数操作符（如 "store" 或 "count()", 或 "index(-1)" 或 "concat('world')", 或 "append({'a':'1'})"）
     */
    private void resolveKey() {
        String segment = parseSegment('.', '[');

        if (segment.isEmpty()) {
            throw new JsonPathException("Expected a segment, wildcard or function at index " + position);
        }

        // 检查是否是函数调用
        int openParenIndex = segment.indexOf('(');
        int closeParenIndex = segment.lastIndexOf(')');

        if (openParenIndex > 0 && closeParenIndex == segment.length() - 1) {
            addSegment(new FuncSegment(segment));
        } else if (segment.equals("*")) {
            addSegment(new SelectSegment(segment));
        } else {
            addSegment(new SelectSegment(segment));
        }
    }


    // 解析路径段（支持终止符列表）
    // 在 JsonPathCompiler.java 中

    /**
     * 解析路径段（支持终止符列表），同时健壮地处理引号、正则、嵌套方括号和Unicode转义。
     *
     * @param terminators 允许的终止字符，如 '.', '[' 或 ']'。
     * @return 解析到的路径段字符串。
     */
    private String parseSegment(char... terminators) {
        StringBuilder sb = new StringBuilder();
        boolean inQuote = false; // 是否在引号内部
        char quoteChar = 0;
        boolean inRegex = false; // 是否在正则表达式内部 (仅在过滤器中可能出现)
        boolean inUnicodeEscape = false; // 是否在Unicode转义序列中
        int unicodeEscapeCount = 0; // Unicode转义字符计数
        StringBuilder unicodeBuffer = new StringBuilder(); // Unicode转义字符缓冲区

        // 检查当前解析是否是针对方括号内部的内容
        boolean parsingBracketContent = isTerminator(']', terminators);
        int bracketLevel = parsingBracketContent ? 1 : 0;

        while (position < path.length()) {
            char ch = path.charAt(position);

            // 1. 处理Unicode转义序列
            if (inUnicodeEscape) {
                unicodeBuffer.append(ch);
                unicodeEscapeCount++;

                if (unicodeEscapeCount == 4) {
                    // 完成4位Unicode转义序列
                    try {
                        int codePoint = Integer.parseInt(unicodeBuffer.toString(), 16);
                        sb.append((char) codePoint);
                    } catch (NumberFormatException e) {
                        // 如果解析失败，保持原样输出
                        sb.append("\\u").append(unicodeBuffer);
                    }
                    inUnicodeEscape = false;
                    unicodeEscapeCount = 0;
                    unicodeBuffer.setLength(0);
                }
                position++;
                continue;
            }

            // 2. 检测Unicode转义序列开始
            if (ch == '\\' && position + 1 < path.length() && path.charAt(position + 1) == 'u') {
                // 开始Unicode转义序列
                inUnicodeEscape = true;
                position += 2; // 跳过 \\u
                continue;
            }

            // 3. 处理引号内的内容
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
                position++;
                continue;
            }

            // 如果在引号内部，则除了上面的引号关闭逻辑，其他所有字符都只追加
            if (inQuote) {
                sb.append(ch);
                position++;
                continue;
            }

            // 4. 处理正则表达式的开始/结束
            if (ch == '/' && !inRegex) {
                inRegex = true;
                sb.append(ch);
                position++;
                continue;
            } else if (ch == '/' && inRegex) {
                inRegex = false;
                sb.append(ch);
                position++;
                continue;
            }

            // 如果在正则表达式内部，忽略终止符检查
            if (inRegex) {
                sb.append(ch);
                position++;
                continue;
            }

            // 5. 处理转义字符（非Unicode）
            if (ch == '\\' && position + 1 < path.length()) {
                // 处理常见的转义序列
                char nextChar = path.charAt(position + 1);
                switch (nextChar) {
                    case '\\':
                        sb.append('\\');
                        position += 2;
                        continue;
                    case '/':
                        sb.append('/');
                        position += 2;
                        continue;
                    case 'b':
                        sb.append('\b');
                        position += 2;
                        continue;
                    case 'f':
                        sb.append('\f');
                        position += 2;
                        continue;
                    case 'n':
                        sb.append('\n');
                        position += 2;
                        continue;
                    case 'r':
                        sb.append('\r');
                        position += 2;
                        continue;
                    case 't':
                        sb.append('\t');
                        position += 2;
                        continue;
                    case '"':
                        sb.append('"');
                        position += 2;
                        continue;
                    case '\'':
                        sb.append('\'');
                        position += 2;
                        continue;
                    default:
                        // 如果不是特殊转义，保持原样
                        sb.append(ch);
                        position++;
                        continue;
                }
            }

            // 6. 处理嵌套的方括号
            if (parsingBracketContent) {
                if (ch == '[') {
                    bracketLevel++;
                } else if (ch == ']') {
                    bracketLevel--;
                    if (bracketLevel == 0) {
                        position++; // 跳过闭合的 ]
                        break;
                    }
                }
            }

            // 7. 检查外部终止符
            if (!parsingBracketContent || bracketLevel == 1) {
                if (isTerminator(ch, terminators)) {
                    // 遇到非方括号的终止符 (如 . 或 [)
                    if (ch != ']') {
                        break;
                    }
                }
            }

            sb.append(ch);
            position++;
        }

        // 处理未完成的Unicode转义序列
        if (inUnicodeEscape) {
            sb.append("\\u").append(unicodeBuffer);
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
        while (position < path.length() && Character.isWhitespace(path.charAt(position))) {
            position++;
        }
    }
}