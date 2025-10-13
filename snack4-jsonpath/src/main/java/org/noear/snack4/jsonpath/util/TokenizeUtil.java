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
package org.noear.snack4.jsonpath.util;

import org.noear.snack4.jsonpath.filter.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * 表达式分词工具
 *
 * @author noear 2025/10/13 created
 * @since 4.0
 */
public class TokenizeUtil {
    /**
     * 分词
     */
    public static List<Expression.Token> tokenize(String filter) {
        List<Expression.Token> tokens = new ArrayList<>();
        int index = 0;
        int len = filter.length();

        while (index < len) {
            char c = filter.charAt(index);
            if (Character.isWhitespace(c)) {
                index++;
                continue;
            }

            if (c == '(') {
                tokens.add(new Expression.Token(Expression.TokenType.LPAREN, "("));
                index++;
            } else if (c == ')') {
                tokens.add(new Expression.Token(Expression.TokenType.RPAREN, ")"));
                index++;
            } else if (c == '&' && index + 1 < len && filter.charAt(index + 1) == '&') {
                tokens.add(new Expression.Token(Expression.TokenType.AND, "&&"));
                index += 2;
            } else if (c == '|' && index + 1 < len && filter.charAt(index + 1) == '|') {
                tokens.add(new Expression.Token(Expression.TokenType.OR, "||"));
                index += 2;
            } else {
                // 扫描 ATOM (逻辑项，如 @.book.price > 10)
                int start = index;
                char quoteChar = 0; // 0 表示不在引号内
                int parenCount = 0;
                boolean hasContent = false;

                while (index < len) {
                    char curr = filter.charAt(index);

                    // 1. 引号处理：处理函数参数或字面量中的引号
                    if (quoteChar != 0) {
                        if (curr == quoteChar) {
                            quoteChar = 0;
                        }
                    } else if (curr == '\'' || curr == '"') {
                        quoteChar = curr;
                    }

                    // 2. 逻辑操作符和括号边界检测 (不在引号内时)
                    else {
                        if (curr == '(') {
                            parenCount++;
                        } else if (curr == ')') {
                            if (parenCount > 0) {
                                parenCount--; // 匹配函数内部的右括号
                            } else {
                                // 独立的右括号，停止当前token，让外层循环处理
                                break;
                            }
                        }

                        // 检查是否遇到顶级逻辑操作符（不在任何括号内时）
                        if (parenCount == 0) {
                            if (curr == '&' && index + 1 < len && filter.charAt(index + 1) == '&') {
                                break; // 遇到顶级 &&，终止 ATOM
                            }
                            if (curr == '|' && index + 1 < len && filter.charAt(index + 1) == '|') {
                                break; // 遇到顶级 ||，终止 ATOM
                            }
                            if (curr == '(') {
                                // 遇到独立的左括号，终止 ATOM，让外层循环处理
                                break;
                            }
                        }

                        if (Character.isWhitespace(curr)) {
                            // 遇到空格，继续扫描直到遇到操作符或非空格
                        }

                    }

                    index++;
                    hasContent = true;
                }

                // 提取 ATOM
                if (hasContent) {
                    String atom = filter.substring(start, index).trim();
                    if (!atom.isEmpty()) {
                        tokens.add(new Expression.Token(Expression.TokenType.ATOM, atom));
                    }
                } else if (index == start) {
                    // 如果没有读取到任何内容，向前移动一位避免无限循环 (理论上不会发生)
                    index++;
                }
            }
        }
        return tokens;
    }

}
