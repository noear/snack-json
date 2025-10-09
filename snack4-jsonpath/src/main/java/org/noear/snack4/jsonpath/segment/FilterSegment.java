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
import org.noear.snack4.jsonpath.PathSource;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.Expression;
import org.noear.snack4.jsonpath.QueryMode;
import org.noear.snack4.jsonpath.Segment;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理过滤器（如 [?(@.price > 10)] ）
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public class FilterSegment implements Segment {
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
    public List<ONode> resolve(QueryContext ctx, List<ONode> currentNodes) {
        if (this.flattened) {
            //已经偏平化
            List<ONode> result = new ArrayList<>();
            for (ONode n1 : currentNodes) {
                if (expression.test(n1, ctx)) {
                    result.add(n1);
                }
            }
            return result;
        } else {
            //还未偏平化
            List<ONode> result = new ArrayList<>();

            if (ctx.getMode() == QueryMode.CREATE && currentNodes.size() == 1) {
                for (ONode n : currentNodes) {
                    if (n.isNull()) {
                        n.asArray().addNew();
                    }

                    flattenResolve(ctx, n, result);
                }
            } else {
                for (ONode n : currentNodes) {
                    flattenResolve(ctx, n, result);
                }
            }

            return result;
        }
    }

    // 新增递归展开方法
    private void flattenResolve(QueryContext ctx, ONode node, List<ONode> result) {
        if (node.isArray()) {
            int idx = 0;
            for (ONode n1 : node.getArray()) {
                if (n1.source == null) {
                    n1.source = new PathSource(node, null, idx);
                }

                flattenResolve(ctx, n1, result);
                idx++;
            }
        } else {
            if (ctx.getMode() == QueryMode.CREATE) {
                if (node.isNull()) {
                    node.asObject();
                }
            }

            if (expression.test(node, ctx)) {
                result.add(node);
            }
        }
    }
}