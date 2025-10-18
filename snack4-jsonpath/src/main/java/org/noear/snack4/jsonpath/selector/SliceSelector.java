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
package org.noear.snack4.jsonpath.selector;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.JsonPathException;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.util.IndexUtil;
import org.noear.snack4.jsonpath.util.RangeUtil;
import org.noear.snack4.jsonpath.util.SelectUtil;

import java.util.List;
import java.util.function.Consumer;

/**
 * (数组)切片选择器（如 $[1:4]，$[1:5:1], $[::1]）
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public class SliceSelector implements Selector {
    //start:end:step
    private final String expr;

    private Integer startRef;
    private Integer endRef;
    private int step;

    private boolean multiple;

    public SliceSelector(String expr) {
        this.expr = expr;
        this.multiple = true;

        String[] parts = expr.split(":", 3); //[start:end:step]
        if (parts.length == 1) {
            throw new JsonPathException("Invalid range syntax: " + expr);
        }

        final int step = (parts.length == 3 && parts[2].length() > 0) ? Integer.parseInt(parts[2]) : 1;

        if (parts[0].length() > 0) {
            startRef = Integer.parseInt(parts[0]);
        }

        if (parts[1].length() > 0) {
            endRef = Integer.parseInt(parts[1]);
        }

        if (startRef != null && endRef != null && startRef > 0 && endRef > 0) {
            if (Math.abs(startRef - endRef) == 1) {
                multiple = false;
            }
        }

        this.step = step;
    }

    @Override
    public String toString() {
        return expr;
    }

    @Override
    public boolean isMultiple() {
        return multiple;
    }

    @Override
    public boolean isExpanded() {
        return false;
    }

    @Override
    public void select(QueryContext ctx, boolean isDescendant, List<ONode> currentNodes, Consumer<ONode> acceptor) {
        if (step == 0) {
            return;
        }

        if (isDescendant) {
            //后代（IETF JSONPath (RFC 9535)：包括“自己”和“后代”）
            SelectUtil.descendantSelect(currentNodes, !ctx.forJaywayMode(), (n1) -> {
                doSlice(ctx, n1, acceptor);
            });
        } else {
            for (ONode n1 : currentNodes) {
                doSlice(ctx, n1, acceptor);
            }
        }
    }

    private void doSlice(QueryContext ctx, ONode node, Consumer<ONode> acceptor) {
        if (node.isArray()) {
            int size = node.getArrayUnsafe().size();
            int start = parseRangeBound(startRef, (step > 0 ? 0 : size - 1), size);
            int end = parseRangeBound(endRef, (step > 0 ? size : -1), size);

            // 调整范围确保有效
            RangeUtil.Bounds bounds = RangeUtil.boundsOf(start, end, step, size);

            if (step > 0) {
                int i = bounds.getLower();
                while (i < bounds.getUpper()) {
                    IndexUtil.forIndexUnsafe(ctx, node, i, acceptor);

                    i += step;
                }
            } else {
                int i = bounds.getUpper();
                while (bounds.getLower() < i) {
                    IndexUtil.forIndexUnsafe(ctx, node, i, acceptor);

                    i += step;
                }
            }
        }
    }

    // 辅助方法：解析范围边界
    private int parseRangeBound(Integer boundRef, int def, int size) {
        if (boundRef == null) {
            return def; // 默认开始
        }

        int bound = boundRef.intValue();
        if (bound < 0) {
            bound += size;
        }
        return bound;
    }
}