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
import org.noear.snack4.jsonpath.QueryContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 逻辑表达式项
 *
 * @author noear 2025/5/5 created
 * @since 4.0
 */
public class Term {
    private static Map<String, Term> conditionMap = new ConcurrentHashMap<>();

    public static Term get(String conditionStr) {
        return conditionMap.computeIfAbsent(conditionStr, Term::new);
    }

    /// ///////////////////


    private final String termStr;

    private final Operand left;
    private final String op;
    private final Operand right;

    private Term(String termStr) {
        this.termStr = termStr.trim(); // 保证整个字符串没有首尾空格

        String leftStr;
        String opStr = null;
        String rightStr = null;

        int parenLevel = 0; // 括号层级计数器
        int endOfLeft = -1; // 左操作数结束位置的索引

        // 1. 寻找左操作数的结束位置
        // 这个位置是第一个在括号层级为0时遇到的空格
        for (int i = 0; i < this.termStr.length(); i++) {
            char c = this.termStr.charAt(i);
            if (c == '(') {
                parenLevel++;
            } else if (c == ')') {
                // 避免括号不匹配导致的负数
                if (parenLevel > 0) {
                    parenLevel--;
                }
            } else if (c == ' ' && parenLevel == 0) {
                endOfLeft = i;
                break; // 找到第一个顶级分隔符，跳出循环
            }
        }

        if (endOfLeft == -1) {
            // 2. 没有找到顶级空格，说明整个字符串都是左操作数
            leftStr = this.termStr;
        } else {
            // 3. 找到了顶级空格，分割出左操作数
            leftStr = this.termStr.substring(0, endOfLeft).trim();

            // 剩余部分包含操作符和右操作数
            String opAndRight = this.termStr.substring(endOfLeft + 1).trim();

            // 寻找操作符和右操作数之间的空格
            // 假设操作符本身不包含空格（例如，我们不支持 'is not' 这样的多词操作符）
            int endOfOp = opAndRight.indexOf(' ');

            if (endOfOp == -1) {
                // 如果没有更多空格，说明剩余部分就是操作符（例如一元操作符）
                opStr = opAndRight;
            } else {
                // 如果还有空格，分割出操作符和右操作数
                opStr = opAndRight.substring(0, endOfOp).trim();
                rightStr = opAndRight.substring(endOfOp + 1).trim();
            }
        }

        this.left = new Operand(leftStr);
        this.op = opStr;
        this.right = new Operand(rightStr);
    }

    /**
     * 左操作元
     */
    public Operand getLeft() {
        return left;
    }

    /**
     * 操作符（可能没有）
     */
    public String getOp() {
        return op;
    }

    /**
     * 右操作元（可能没有）
     */
    public Operand getRight() {
        return right;
    }

    public ONode getLeftNode(QueryContext ctx, ONode node) {
        if (left.getQuery() != null) {
            return ctx.nestedQuery(node, left.getQuery());
        } else {
            return left.getNode();
        }
    }


    public ONode getRightNode(QueryContext ctx, ONode node) {
        if (right.getQuery() != null) {
            return ctx.nestedQuery(node, right.getQuery());
        } else {
            return right.getNode();
        }
    }


    @Override
    public String toString() {
        return termStr;
    }
}