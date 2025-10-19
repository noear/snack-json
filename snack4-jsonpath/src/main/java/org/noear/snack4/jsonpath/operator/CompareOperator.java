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
import org.noear.snack4.jsonpath.Operator;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.QueryMode;
import org.noear.snack4.jsonpath.filter.Term;

import java.util.Objects;

/**
 *
 * @author noear 2025/10/13 created
 * @since 4.0
 */
public class CompareOperator implements Operator {
    private final CompareType type;

    public CompareOperator(CompareType type) {
        this.type = type;
    }

    @Override
    public boolean apply(QueryContext ctx, ONode node, Term term) {
        ONode leftNode = term.getLeftNode(ctx, node);
        ONode rightNode = term.getRightNode(ctx, node);

        if (rightNode.isNull()) {
            //右侧为 null
            return compareNull(leftNode);
        } else if (leftNode.isNull()) {
            //左侧为 null
            if (ctx.getMode() == QueryMode.CREATE) {
                if (type.hasEq()) {
                    leftNode.fill(rightNode);
                    return true;
                }
            }

            return compareNull(rightNode);
        }

        if (leftNode.type() == rightNode.type()) {
            if (leftNode.isString()) {
                return compareString(leftNode.getValueAs(), rightNode.getValueAs());
            } else if (leftNode.isNumber()) {
                //都是数字
                return compareNumber(leftNode.getValueAs(), rightNode.getValueAs());
            } else if (leftNode.isNull()) {
                return compareNumber(0, 0);
            } else {
                if (type == CompareType.NEQ) {
                    return leftNode.equals(rightNode) == false;
                } else if (type.hasEq()) {
                    return leftNode.equals(rightNode);
                }
            }
        } else {
            if (leftNode.isNull()) {
                return false;
            }

            if (type == CompareType.NEQ) {
                return true;
            }
        }

        return false;
    }

    private boolean compareNull(ONode a) {
        if (type.hasEq()) {
            return a.isNull();
        } else if (type == CompareType.NEQ) {
            return a.isNull() == false;
        } else {
            return false;
        }
    }

    private boolean compareString(String a, String b) {
        int cmp = a.compareTo(b);

        switch (type) {
            case EQ:
                return cmp == 0;
            case NEQ:
                return cmp != 0;
            case GT:
                return cmp > 0;
            case GTE:
                return cmp >= 0;
            case LT:
                return cmp < 0;
            case LTE:
                return cmp <= 0;
            default:
                throw new JsonPathException("Unsupported operator for string: " + type.getCode());
        }
    }

    private boolean compareNumber(Number a0, Number b0) {
        double a = a0.doubleValue();
        double b = b0.doubleValue();

        int cmp = Double.compare(a, b);

        switch (type) {
            case EQ:
                return cmp == 0;
            case NEQ:
                return cmp != 0;
            case GT:
                return cmp > 0;
            case GTE:
                return cmp >= 0;
            case LT:
                return cmp < 0;
            case LTE:
                return cmp <= 0;
            default:
                throw new JsonPathException("Unsupported operator for number: " + type.getCode());
        }
    }
}