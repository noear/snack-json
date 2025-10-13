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
import org.noear.snack4.jsonpath.util.TermUtil;

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

    private final boolean not;
    private final Operand left;
    private final String op;
    private final Operand right;

    private Term(String expr) {
        this.termStr = expr; // 仅用于打印

        expr = expr.trim();

        if (expr.charAt(0) == '!') {
            not = true;
            expr = expr.substring(1);
        } else {
            not = false;
        }

        if(expr.length() > 3) {
            if (expr.charAt(0) == '(' && expr.charAt(expr.length() - 1) == ')') { //like !(xxx == yyy)
                expr = expr.substring(1, expr.length() - 1);
            }
        }

        String[] result = TermUtil.resolve(expr);

        this.left = new Operand(result[0]);
        this.op = result[1];
        this.right = new Operand(result[2]);
    }

    public boolean isNot() {
        return not;
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
        return left.getNode(ctx, node);
    }


    public ONode getRightNode(QueryContext ctx, ONode node) {
        return right.getNode(ctx, node);
    }


    @Override
    public String toString() {
        return termStr;
    }
}