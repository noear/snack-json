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
package org.noear.snack4.jsonpath.segment;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.JsonPathException;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.util.RangeUtil;
import org.noear.snack4.jsonpath.Segment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 数组切片选择器（如 $.list[1:4]，[1:5:1]）
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public class ArraySliceSegment implements Segment {
    //[start:end:step]
    private final String segmentStr;
    private Integer startRef;
    private Integer endRef;
    private int step;

    public ArraySliceSegment(String segmentStr) {
        this.segmentStr = segmentStr;

        String[] parts = segmentStr.split(":", 3); //[start:end:step]
        if (parts.length == 1) {
            throw new JsonPathException("Invalid range syntax: " + segmentStr);
        }

        final int step = (parts.length == 3 && parts[2].length() > 0) ? Integer.parseInt(parts[2]) : 1;

        if (parts[0].length() > 0) {
            startRef = Integer.parseInt(parts[0]);
        }

        if (parts[1].length() > 0) {
            endRef = Integer.parseInt(parts[1]);
        }

        this.step = step;
    }

    @Override
    public String toString() {
        return "[" + segmentStr + "]";
    }

    @Override
    public List<ONode> resolve(QueryContext ctx, List<ONode> currentNodes) {
        if (step == 0) {
            return Collections.emptyList();
        }

        List<ONode> result = new ArrayList<>();

        for (ONode arr : currentNodes) {
            doResolve(ctx, arr, result);
        }

        ctx.flattened = false;
        return result;
    }

    private void doResolve(QueryContext ctx, ONode node, List<ONode> result) {
        if (node.isArray()) {
            int size = node.size();
            int start = parseRangeBound(startRef, (step > 0 ? 0 : size - 1), size);
            int end = parseRangeBound(endRef, (step > 0 ? size : -1), size);

            // 调整范围确保有效
            RangeUtil.Bounds bounds = RangeUtil.bounds(start, end, step, size);

            if (step > 0) {
                int i = bounds.getLower();
                while (i < bounds.getUpper()) {
                    IndexUtil.forIndexUnsafe(ctx, node, i, result);

                    i += step;
                }
            } else {
                int i = bounds.getUpper();
                while (bounds.getLower() < i) {
                    IndexUtil.forIndexUnsafe(ctx, node, i, result);

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