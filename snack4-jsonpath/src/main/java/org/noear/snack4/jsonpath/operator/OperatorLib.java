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
package org.noear.snack4.jsonpath.operator;


import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.JsonPathException;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.QueryMode;
import org.noear.snack4.jsonpath.filter.Term;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JsonPath 操作符库(支持动态注册)
 *
 * @author noear 2025/5/5 created
 * @since 4.0
 */
public class OperatorLib {
    private static final Map<String, Operator> LIB = new ConcurrentHashMap<>();

    static {
        //协议规定
        register("==", OperatorLib::compare);
        register("!=", OperatorLib::compare);
        register(">", OperatorLib::compare);
        register("<", OperatorLib::compare);
        register(">=", OperatorLib::compare);
        register("<=", OperatorLib::compare);

        //扩展
        register("=~", new MatchesOperator());

        register("in", new InOperator());
        register("nin", new NinOperator());

        register("startsWith", new StartsWithOperator());
        register("endsWith", new EndsWithOperator());

        register("contains", new ContainsOperator());
    }

    /**
     * 注册
     */
    public static void register(String name, Operator func) {
        LIB.put(name, func);
    }

    /**
     * 获取
     */
    public static Operator get(String funcName) {
        return LIB.get(funcName);
    }

    /// /////////////////

    private static boolean compare(QueryContext ctx, ONode node, Term term) {
        ONode leftNode = term.getLeftNode(ctx, node);
        ONode rightNode = term.getRightNode(ctx, node);

        if (leftNode.getType() == rightNode.getType()) {
            if (leftNode.isString()) {
                return compareString(term.getOp(), leftNode, rightNode);
            } else if (leftNode.isNumber()) {
                //都是数字
                return compareNumber(term.getOp(), leftNode.getDouble(), rightNode.getDouble());
            } else if (leftNode.isNull()) {
                return compareNumber(term.getOp(), 0, 0);
            } else {
                if ("!=".equals(term.getOp())) {
                    return leftNode.equals(rightNode) == false;
                } else if (term.getOp().indexOf('=') >= 0) {
                    return leftNode.equals(rightNode);
                }
            }
        } else {
            if (ctx.getMode() == QueryMode.CREATE && leftNode.isNull()) {
                if ("==".equals(term.getOp())) {
                    leftNode.fill(rightNode);
                    return true;
                }
            }

            if ("!=".equals(term.getOp())) {
                return true;
            }
        }

        return false;
    }

    /// ///////////////


    private static boolean compareString(String op, ONode a, ONode b) {
        switch (op) {
            case "==":
                return Objects.equals(a.getString(), b.getString());
            case "!=":
                return !Objects.equals(a.getString(), b.getString());
            case ">":
                return Objects.compare(a.getString(), b.getString(), String::compareTo) > 0;
            case "<":
                return Objects.compare(a.getString(), b.getString(), String::compareTo) < 0;
            case ">=":
                return Objects.compare(a.getString(), b.getString(), String::compareTo) >= 0;
            case "<=":
                return Objects.compare(a.getString(), b.getString(), String::compareTo) <= 0;

            default:
                throw new JsonPathException("Unsupported operator for string: " + op);
        }
    }

    private static boolean compareNumber(String op, double a, double b) {
        switch (op) {
            case "==":
                return a == b;
            case "!=":
                return a != b;
            case ">":
                return a > b;
            case "<":
                return a < b;
            case ">=":
                return a >= b;
            case "<=":
                return a <= b;
            default:
                throw new JsonPathException("Unsupported operator for number: " + op);
        }
    }
}