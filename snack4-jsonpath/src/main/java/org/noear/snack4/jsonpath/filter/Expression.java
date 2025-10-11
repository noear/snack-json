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
package org.noear.snack4.jsonpath.filter;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.JsonPathException;
import org.noear.snack4.jsonpath.Operation;
import org.noear.snack4.jsonpath.OperationLib;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.util.Asserts;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 逻辑表达式
 *
 * @author noear 2025/5/5 created
 * @since 4.0
 */
public class Expression {
    private static Map<String, Expression> expressionMap = new ConcurrentHashMap<>();

    public static Expression get(String expressionStr) {
        return expressionMap.computeIfAbsent(expressionStr, Expression::new);
    }

    /// ///////////////////
    private final String expressionStr;
    private final List<Token> rpn;

    private Expression(String expressionStr) {
        this.expressionStr = expressionStr;
        List<Token> tokens = tokenize(expressionStr);
        this.rpn = convertToRPN(tokens);
    }

    @Override
    public String toString() {
        return expressionStr;
    }

    // 评估逆波兰式
    public boolean test(ONode node, QueryContext ctx) {
        try {
            Deque<Boolean> stack = new ArrayDeque<>();
            for (Token token : rpn) {
                if (token.type == TokenType.ATOM) {
                    stack.push(evaluateTerm(ctx, node, token.value));
                } else if (token.type == TokenType.AND || token.type == TokenType.OR) {
                    boolean b = stack.pop();
                    boolean a = stack.pop();
                    stack.push(token.type == TokenType.AND ? a && b : a || b);
                }
            }
            return stack.pop();
        } catch (Throwable ex) {
            return false;
        }
    }

    /**
     * 分词
     */
    public static List<Token> tokenize(String filter) {
        List<Token> tokens = new ArrayList<>();
        int index = 0;
        int len = filter.length();

        while (index < len) {
            char c = filter.charAt(index);
            if (Character.isWhitespace(c)) {
                index++;
                continue;
            }

            // 检查是否是独立的右括号（不在函数调用内部的）
            if (c == ')') {
                // 检查前面是否有对应的函数调用
                boolean isFunctionParen = false;
                if (!tokens.isEmpty()) {
                    Token lastToken = tokens.get(tokens.size() - 1);
                    // 如果前一个token是函数调用的开始部分，那么这个右括号属于函数调用
                    if (lastToken.type == TokenType.ATOM &&
                            (lastToken.value.contains("(") && !lastToken.value.contains(")"))) {
                        isFunctionParen = true;
                    }
                }

                if (!isFunctionParen) {
                    tokens.add(new Token(TokenType.RPAREN, ")"));
                    index++;
                    continue;
                }
                // 如果是函数调用的右括号，继续处理作为ATOM的一部分
            }

            if (c == '(') {
                tokens.add(new Token(TokenType.LPAREN, "("));
                index++;
            } else if (c == '&' && index + 1 < len && filter.charAt(index + 1) == '&') {
                tokens.add(new Token(TokenType.AND, "&&"));
                index += 2;
            } else if (c == '|' && index + 1 < len && filter.charAt(index + 1) == '|') {
                tokens.add(new Token(TokenType.OR, "||"));
                index += 2;
            } else {
                int start = index;
                boolean inQuotes = false;
                int parenCount = 0;
                boolean hasContent = false;

                while (index < len) {
                    char curr = filter.charAt(index);

                    // 处理引号内的内容
                    if (curr == '\'' || curr == '"') {
                        inQuotes = !inQuotes;
                    }

                    // 如果不在引号内，检查括号和操作符
                    if (!inQuotes) {
                        if (curr == '(') {
                            parenCount++;
                            hasContent = true;
                        } else if (curr == ')') {
                            if (parenCount > 0) {
                                parenCount--; // 匹配函数内部的右括号
                            } else {
                                // 独立的右括号，停止当前token
                                break;
                            }
                        }

                        // 检查是否遇到逻辑操作符（不在括号内时）
                        if (parenCount == 0) {
                            if (curr == '&' && index + 1 < len && filter.charAt(index + 1) == '&') {
                                break;
                            }
                            if (curr == '|' && index + 1 < len && filter.charAt(index + 1) == '|') {
                                break;
                            }
                            if (curr == '(' || curr == ')') {
                                // 独立的括号，已经在上面的条件中处理
                                if (curr == '(') {
                                    // 独立的左括号，需要单独处理
                                    if (index == start) {
                                        index++; // 让外层循环处理这个左括号
                                        break;
                                    } else {
                                        // 当前token已经有一些内容，遇到独立的左括号就停止
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    index++;
                    hasContent = true;
                }

                // 如果没有读取到任何内容，向前移动一位避免无限循环
                if (!hasContent && index == start) {
                    index++;
                } else if (hasContent) {
                    String atom = filter.substring(start, index).trim();
                    if (!atom.isEmpty()) {
                        tokens.add(new Token(TokenType.ATOM, atom));
                    }
                }
            }
        }
        return tokens;
    }

    /**
     * 转换为逆波兰式
     */
    public static List<Token> convertToRPN(List<Token> tokens) {
        List<Token> output = new ArrayList<>();
        Deque<Token> stack = new ArrayDeque<>();

        for (Token token : tokens) {
            switch (token.type) {
                case ATOM:
                    output.add(token);
                    break;
                case LPAREN:
                    stack.push(token);
                    break;
                case RPAREN:
                    while (!stack.isEmpty() && stack.peek().type != TokenType.LPAREN) {
                        output.add(stack.pop());
                    }
                    stack.pop();
                    break;
                case AND:
                case OR:
                    while (!stack.isEmpty() && stack.peek().type != TokenType.LPAREN &&
                            precedence(token) <= precedence(stack.peek())) {
                        output.add(stack.pop());
                    }
                    stack.push(token);
                    break;
            }
        }

        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }
        return output;
    }

    private static int precedence(Token token) {
        return token.type == TokenType.AND ? 2 : token.type == TokenType.OR ? 1 : 0;
    }

    private boolean evaluateTerm(QueryContext ctx, ONode node, String termStr) {
        Term term = Term.get(termStr);

        if (term.isNot()) {
            return !doEvaluateTerm(ctx, node, term);
        } else {
            return doEvaluateTerm(ctx, node, term);
        }
    }


    private boolean doEvaluateTerm(QueryContext ctx, ONode node, Term term) {
        // 过滤空条件（操作符处理时，就不需要再过滤了）
        if (Asserts.isEmpty(term.getLeft().getValue())) {
            return false;
        }

        // 单元操作（如 @.price）
        if (Asserts.isEmpty(term.getRight().getValue())) {
            if (term.getOp() == null) {
                ONode leftNode = term.getLeftNode(ctx, node);
                return !leftNode.isNull();
            } else {
                return false;
            }
        }

        Operation operation = OperationLib.get(term.getOp());

        if (operation == null) {
            throw new JsonPathException("Unsupported operator : " + term.getOp());
        }

        return operation.apply(ctx, node, term);
    }

    private enum TokenType {ATOM, AND, OR, LPAREN, RPAREN}

    public static class Token {
        final TokenType type;
        final String value;

        Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
}