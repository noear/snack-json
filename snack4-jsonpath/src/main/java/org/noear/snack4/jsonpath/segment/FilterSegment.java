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
import org.noear.snack4.jsonpath.Context;
import org.noear.snack4.jsonpath.Expression;
import org.noear.snack4.jsonpath.QueryMode;
import org.noear.snack4.jsonpath.SegmentFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理过滤器（如 [?(@.price > 10)]）
 *
 * @author noear 2025/10/3 created
 */
public class FilterSegment implements SegmentFunction {
    private final Expression expression;
    private final boolean flattened;

    /**
     * @param segmentStr `?...`
     */
    public FilterSegment(String segmentStr, boolean flattened) {
        this.expression = Expression.get(segmentStr.substring(1));
        this.flattened = flattened;
    }

    @Override
    public List<ONode> resolve(List<ONode> currentNodes, Context context, QueryMode mode) {
        if (this.flattened) {
            //已经偏平化
            List<ONode> result = new ArrayList<>();
            for (ONode n : currentNodes) {
                if (expression.test(n, context.root)) {
                    result.add(n);
                }
            }
            return result;
        } else {
            //还未偏平化
            List<ONode> result = new ArrayList<>();

            for (ONode n : currentNodes) {
                flattenResolve(n, context, result);
            }

            return result;
        }
    }

    // 新增递归展开方法
    private void flattenResolve(ONode node, Context context, List<ONode> result) {
        if (node.isArray()) {
            for (ONode n1 : node.getArray()) {
                flattenResolve(n1, context, result);
            }
        } else if (node.isNull() == false) {
            if (expression.test(node, context.root)) {
                result.add(node);
            }
        }
    }
}