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
import org.noear.snack4.jsonpath.Operator;
import org.noear.snack4.jsonpath.OperatorLib;
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
            throw new JsonPathException(ex);
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

            if (c == '(') {
                tokens.add(new Token(TokenType.LPAREN, "("));
                index++;
            } else if (c == ')') {
                tokens.add(new Token(TokenType.RPAREN, ")"));
                index++;
            } else if (c == '&' && index + 1 < len && filter.charAt(index + 1) == '&') {
                tokens.add(new Token(TokenType.AND, "&&"));
                index += 2;
            } else if (c == '|' && index + 1 < len && filter.charAt(index + 1) == '|') {
                tokens.add(new Token(TokenType.OR, "||"));
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
                        tokens.add(new Token(TokenType.ATOM, atom));
                    }
                } else if (index == start) {
                    // 如果没有读取到任何内容，向前移动一位避免无限循环 (理论上不会发生)
                    index++;
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

    /**
     * 优先级
     * */
    private static int precedence(Token token) {
        return token.type == TokenType.AND ? 2 : token.type == TokenType.OR ? 1 : 0;
    }

    private boolean evaluateTerm(QueryContext ctx, ONode node, String termStr) {
        Term term = Term.get(termStr);

        boolean result = doEvaluateTerm(ctx, node, term);
        return term.isNot() ? !result : result;
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
                return confirmQuery(node, leftNode);
            } else {
                return false;
            }
        }

        Operator operation = OperatorLib.get(term.getOp());

        if (operation == null) {
            throw new JsonPathException("Unsupported operator : " + term.getOp());
        }

        return operation.apply(ctx, node, term);
    }

    private boolean confirmQuery(ONode node, ONode leftNode) { //node 方便调试
        if (leftNode.isBoolean()) {
            return leftNode.getBoolean();
        }

        if (leftNode.isArray()) {
            return leftNode.size() > 0;
        }

        return !leftNode.isNull();
    }

    private static enum TokenType {ATOM, AND, OR, LPAREN, RPAREN}

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