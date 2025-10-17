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
 * JsonPath 解析器
 *
 * @author noear
 * @since 4.0
 * */
public class JsonPathParser {
    /*
     * 解析
     * */
    public static JsonPath parse(String path) {
        return new JsonPathParser(path).doParse();
    }

    private final String path;
    private int position;
    private List<Segment> segments = new ArrayList<>();
    private Segment lastSegment = null;

    private JsonPathParser(String path) {
        this.path = path;
    }

    private void addSegment(AbstractSegment segment) {
        segment.before(lastSegment);
        lastSegment = segment;

        segments.add(segment);
    }

    private JsonPath doParse() {
        position = 1; //Skip $, @

        while (position < path.length()) {
            skipWhitespace();
            if (position >= path.length()) break;

            char ch = path.charAt(position);
            if (ch == '.') {
                resolveDot();
            } else if (ch == '[') {
                resolveBracket();
            } else {
                throw new JsonPathException("Unexpected character '" + ch + "' at index " + position);
            }
        }

        return new JsonPath(path, segments);
    }

    /**
     * 分析 '.' 或 '..' 操作符
     */
    private void resolveDot() {
        position++;
        if (position < path.length() && path.charAt(position) == '.') {
            addSegment(new DescendantSegment());

            while (position < path.length()) {
                skipWhitespace();
                if (position >= path.length()) break;
                char ch = path.charAt(position);
                if (ch == '.' || ch == '[') {
                    if (ch == '.') {
                        resolveDot();
                    } else if (ch == '[') {
                        resolveBracket();
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
                resolveBracket();
            } else {
                resolveKey();
            }
        }
    }

    /**
     * 分析 '[...]' 操作符
     */
    private void resolveBracket() {
        position++; // 跳过'['
        String segment = parseSegment(']');
        while (position < path.length() && path.charAt(position) == ']') {
            position++;
        }

        addSegment(new SelectSegment(segment));
    }

    /**
     * 分析键名或函数操作符（如 "store" 或 "count()", 或 "index(-1)" 或 "concat(9.9)" 或 "concat('world')", 或 "append({'a':'1'})"）
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
            addSegment(new SelectSegment("'" + segment + "'"));
        }
    }

    /**
     * 解析路径段（支持终止符列表），同时健壮地处理引号、正则、嵌套方括号、花括号、圆括号和Unicode转义。
     *
     * @param terminators 允许的终止字符，如 '.', '[' 或 ']'。
     * @return 解析到的路径段字符串。
     */
    private String parseSegment(char... terminators) {
        StringBuilder sb = new StringBuilder();
        boolean inQuote = false;
        char quoteChar = 0;
        boolean inRegex = false;

        // Unicode 转义追踪
        boolean inUnicodeEscape = false;
        int unicodeEscapeCount = 0;
        StringBuilder unicodeBuffer = new StringBuilder();

        // 平衡计数器
        // 检查当前解析是否是针对方括号内部的内容
        boolean parsingBracketContent = isTerminator(']', terminators);
        int bracketLevel = parsingBracketContent ? 1 : 0; // 追踪 []
        int parenthesesLevel = 0; // 追踪函数参数的 ()
        int curlyBraceLevel = 0;    // 追踪 JSON 对象参数的 {}

        while (position < path.length()) {
            char ch = path.charAt(position);

            // 1. 处理Unicode转义序列
            if (inUnicodeEscape) {
                unicodeBuffer.append(ch);
                unicodeEscapeCount++;

                if (unicodeEscapeCount == 4) {
                    try {
                        int codePoint = Integer.parseInt(unicodeBuffer.toString(), 16);
                        sb.append((char) codePoint);
                    } catch (NumberFormatException e) {
                        // 确保即使解析失败，原始序列也保留在结果中
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
                inUnicodeEscape = true;
                position += 2; // 跳过 \\u
                continue;
            }

            // 3. 处理非 Unicode 转义字符 (如 \', \", \\, \n)
            if (ch == '\\' && position + 1 < path.length()) {
                // 始终将反斜杠和下一个字符一起加入，这通常是处理转义的简便方法
                // 实际应用中，如果解析器要负责转义字符的解码，应该进行解码
                // 为兼容原有的 switch 逻辑，这里只进行简单的跳过和追加，保持解析的完整性
                char nextChar = path.charAt(position + 1);

                // 如果是已知的转义序列，可以进行解码（这里保持原样以便后续的逻辑能看到转义后的字符，但要确保不影响引号判断）
                if (inQuote || inRegex) {
                    sb.append(ch);
                    sb.append(nextChar);
                    position += 2;
                    continue;
                }
            }


            // 4. 处理引号内的内容
            if ((ch == '\'' || ch == '\"') && !inRegex) {
                if (inQuote && ch == quoteChar) {
                    inQuote = false;
                    quoteChar = 0;
                } else if (!inQuote) {
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

            // 5. 处理正则表达式的开始/结束 (仅当在方括号内容中时才可能是正则表达式)
            if (ch == '/' && parsingBracketContent) {
                if (!inRegex) {
                    inRegex = true;
                } else {
                    inRegex = false;
                }
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

            // 6. 处理括号平衡 (圆括号和花括号) - 必须在检查终止符之前
            if (ch == '(') {
                parenthesesLevel++;
            } else if (ch == ')') {
                if (parenthesesLevel > 0) {
                    parenthesesLevel--;
                }
            }

            if (ch == '{') {
                curlyBraceLevel++;
            } else if (ch == '}') {
                if (curlyBraceLevel > 0) {
                    curlyBraceLevel--;
                }
            }

            // 7. 处理嵌套的方括号 (主要针对方括号段内部的逻辑)
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

            // 8. 检查外部终止符（仅在所有平衡计数器都为 0 时允许终止）
            boolean isBalanced = (parenthesesLevel == 0 && curlyBraceLevel == 0);

            if (isBalanced) {
                // 检查外部段终止符（如在 $.key. 时，终止符是 . 或 [）
                if (!parsingBracketContent && isTerminator(ch, terminators)) {
                    break;
                }
                // 检查方括号内部的终止符（如在 [index] 时，终止符是 ]）
                if (parsingBracketContent && bracketLevel == 1 && isTerminator(ch, terminators)) {
                    // 当在最外层方括号内，且遇到 ] 时，在 7. 中已经被处理和 break
                    // 这里主要是防止在最外层方括号内，遇到 . 或 [ 终止 (但通常只会遇到 ])
                    if (ch != ']') {
                        break; // 理论上不会发生，但以防万一
                    }
                }
            }

            // 确保处理转义字符
            if (ch == '\\' && position + 1 < path.length()) {
                // 再次执行转义字符的简单追加（因为之前的 inQuote/inRegex 检查是排他的）
                sb.append(ch);
                sb.append(path.charAt(position + 1));
                position += 2;
                continue;
            }


            sb.append(ch);
            position++;
        }

        // 处理未完成的Unicode转义序列
        if (inUnicodeEscape) {
            sb.append("\\u").append(unicodeBuffer);
        }

        // 修复：如果循环提前终止，需要检查 position 是否指向下一个操作符
        // 如果 position 已经在 path 外部，或者指向的操作符是终止符，则无需额外处理。


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