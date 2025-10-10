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


import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.filter.Condition;
import org.noear.snack4.jsonpath.util.JsRegexUtil;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JsonPath 操作符库(支持动态注册)
 *
 * @author noear 2025/5/5 created
 * @since 4.0
 */
public class OperationLib {
    private static final Map<String, Operation> LIB = new ConcurrentHashMap<>();

    static {
        // 操作函数
        register("startsWith", OperationLib::startsWith);
        register("endsWith", OperationLib::endsWith);

        register("contains", OperationLib::contains);

        register("in", OperationLib::in);
        register("nin", OperationLib::nin);

        register("=~", OperationLib::matches);

        register("==", OperationLib::compare);
        register("!=", OperationLib::compare);
        register(">", OperationLib::compare);
        register("<", OperationLib::compare);
        register(">=", OperationLib::compare);
        register("<=", OperationLib::compare);
    }

    /**
     * 注册
     */
    public static void register(String name, Operation func) {
        LIB.put(name, func);
    }

    /**
     * 获取
     */
    public static Operation get(String funcName) {
        return LIB.get(funcName);
    }

    /// /////////////////

    private static boolean startsWith(QueryContext ctx, ONode node, Condition condition) {
        ONode leftNode = condition.getLeftNode(ctx, node);

        if (leftNode.isString()) {
            ONode rightNode = condition.getRightNode(ctx, node);
            if (rightNode.isNull()) {
                return false;
            }

            return leftNode.getString().startsWith(rightNode.getString());
        }
        return false;
    }

    private static boolean endsWith(QueryContext ctx, ONode node, Condition condition) {
        ONode leftNode = condition.getLeftNode(ctx, node);

        if (leftNode.isString()) {
            ONode rightNode = condition.getRightNode(ctx, node);
            if (rightNode.isArray()) {
                return false;
            }

            return leftNode.getString().endsWith(rightNode.getString());
        }
        return false;
    }

    private static boolean contains(QueryContext ctx, ONode node, Condition condition) {
        ONode leftNode = condition.getLeftNode(ctx, node);

        ONode expectedNode = condition.getRightNode(ctx, node);

        // 支持多类型包含检查
        if (leftNode.isArray()) {
            return leftNode.getArray().stream()
                    .anyMatch(item -> isValueMatch(item, expectedNode));
        } else if (leftNode.isString()) {
            if (expectedNode.isString()) {
                return leftNode.getString().contains(expectedNode.getString());
            } else {
                throw new IllegalArgumentException("expected string but got " + expectedNode);
            }
        }
        return false;
    }

    private static boolean in(QueryContext ctx, ONode node, Condition condition) {
        ONode leftNode = condition.getLeftNode(ctx, node);

        if (leftNode.isNull() == false) {
            ONode rightNode = condition.getRightNode(ctx, node);
            if (rightNode.isArray() == false) {
                return false;
            }

            return rightNode.getArray().stream().anyMatch(v -> isValueMatch(leftNode, v));
        }

        return false;
    }

    private static boolean nin(QueryContext ctx, ONode node, Condition condition) {
        ONode leftNode = condition.getLeftNode(ctx, node);

        if (leftNode.isNull() == false) {
            ONode rightNode = condition.getRightNode(ctx, node);
            if (rightNode.isArray() == false) {
                return false;
            }

            return rightNode.getArray().stream().noneMatch(v -> isValueMatch(leftNode, v));
        }

        return false;
    }

    private static boolean matches(QueryContext ctx, ONode node, Condition condition) {
        ONode leftNode = condition.getLeftNode(ctx, node);
        ONode rightNode = condition.getRightNode(ctx, node);

        boolean found = false;
        if (leftNode.isValue()) {
            if (rightNode.isString()) {
                String v = rightNode.getString();//.replace("\\/", "/");
                found = JsRegexUtil.of(v).matcher(leftNode.getString()).find();
            }
        }

        return found;
    }

    private static boolean compare(QueryContext ctx, ONode node, Condition condition) {
        ONode leftNode = condition.getLeftNode(ctx, node);
        ONode rightNode = condition.getRightNode(ctx, node);

        // 类型判断逻辑
        if (rightNode.isString()) {
            return compareString(condition.getOp(), leftNode, rightNode);
        } else {
            if (leftNode.getType() == rightNode.getType()) {
                if (leftNode.isNumber()) {
                    //都是数字
                    return compareNumber(condition.getOp(), leftNode.getDouble(), rightNode.getDouble());
                } else if (leftNode.isNull()) {
                    return compareNumber(condition.getOp(), 0, 0);
                } else {
                    if ("!=".equals(condition.getOp())) {
                        return leftNode.equals(rightNode) == false;
                    } else if (condition.getOp().indexOf('=') >= 0) {
                        return leftNode.equals(rightNode);
                    }
                }
            } else {
                if (ctx.getMode() == QueryMode.CREATE && leftNode.isNull()) {
                    if ("==".equals(condition.getOp())) {
                        leftNode.fill(rightNode);
                        return true;
                    }
                }

                if ("!=".equals(condition.getOp())) {
                    return true;
                }
            }

            return false;
        }
    }

    /// ///////////////


    private static boolean compareString(String op, ONode a, ONode b) {
        switch (op) {
            case "==":
                return a.getType() == b.getType() && Objects.equals(a.getString(), b.getString());
            case "!=":
                return a.getType() != b.getType() || !Objects.equals(a.getString(), b.getString());
            case ">":
                return a.getType() == b.getType() && Objects.compare(a.getString(), b.getString(), String::compareTo) > 0;
            case "<":
                return a.getType() == b.getType() && Objects.compare(a.getString(), b.getString(), String::compareTo) < 0;
            case ">=":
                return a.getType() == b.getType() && Objects.compare(a.getString(), b.getString(), String::compareTo) >= 0;
            case "<=":
                return a.getType() == b.getType() && Objects.compare(a.getString(), b.getString(), String::compareTo) <= 0;

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

    private static boolean isValueMatch(ONode item, ONode expected) {
        if (item.isArray()) {
            return item.getArray().stream().anyMatch(one -> isValueMatch(one, expected));
        }

        if (item.isString()) {
            if (expected.isString()) {
                return item.getString().equals(expected.getString());
            }
        } else if (item.isNumber()) {
            if (expected.isNumber()) {
                double itemValue = item.getDouble();
                double expectedValue = expected.getNumber().doubleValue();
                return itemValue == expectedValue;
            }
        } else if (item.isBoolean()) {
            if (expected.isBoolean()) {
                Boolean itemBool = item.getBoolean();
                return itemBool == expected.getBoolean();
            }
        }

        return false;
    }
}