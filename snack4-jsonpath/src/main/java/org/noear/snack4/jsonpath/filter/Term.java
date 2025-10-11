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
        this.termStr = termStr;

        String[] parts = new String[3];

        int spaceIdx = termStr.indexOf(' ');
        if (spaceIdx < 0) {
            //没有空隔
            parts[0] = termStr;
        } else {
            //有空隔
            parts[0] = termStr.substring(0, spaceIdx);
            parts[1] = termStr.substring(spaceIdx + 1).trim();
            spaceIdx = parts[1].indexOf(' ');
            if (spaceIdx > 0) {
                //有第二个空隔
                parts[2] = parts[1].substring(spaceIdx + 1).trim();
                parts[1] = parts[1].substring(0, spaceIdx);
            }
        }

        this.left = new Operand(parts[0]);
        this.op = parts[1];
        this.right = new Operand(parts[2]);
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