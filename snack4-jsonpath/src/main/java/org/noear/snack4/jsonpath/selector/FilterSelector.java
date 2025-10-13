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

import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.PathSource;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.QueryMode;
import org.noear.snack4.jsonpath.filter.Expression;
import org.noear.snack4.jsonpath.util.SelectUtil;

import java.util.List;
import java.util.Map;

/**
 * 过滤选择器：使用逻辑表达式选择特定的子项（如 $[?(@.price > 10)], $..[?(@.price > 10)] ）
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public class FilterSelector implements Selector {
    private final String expr;
    private final Expression expression;

    public FilterSelector(String expr) {
        this.expr = expr;
        this.expression = Expression.get(expr.substring(1));
    }

    @Override
    public String toString() {
        return expr;
    }

    @Override
    public boolean isMultiple() {
        return true;
    }

    public void select(QueryContext ctx, boolean isDescendant, List<ONode> currentNodes, List<ONode> results) {
        boolean forJayway = ctx.hasFeature(Feature.JsonPath_JaywayMode);

        if (isDescendant) {
            //后裔
            SelectUtil.descendantSelect(currentNodes, !forJayway, (n1) -> {
                if (expression.test(n1, ctx)) {
                    results.add(n1);
                }
            });
        } else {
            if (ctx.getMode() == QueryMode.CREATE && currentNodes.size() == 1) {
                ONode n = currentNodes.get(0);

                if (n.isNull()) {
                    n.asArray().addNew();
                }
            }

            for (ONode n : currentNodes) {
                if (forJayway) {
                    flattenResolveJayway(ctx, n, results);
                } else {
                    flattenResolveIetf(ctx, n, results);
                }
            }
        }
    }

    // 新增递归展开方法
    private void flattenResolveJayway(QueryContext ctx, ONode node, List<ONode> result) {
        if (node.isArray()) {
            int idx = 0;
            for (ONode n1 : node.getArray()) {
                if (n1.source == null) {
                    n1.source = new PathSource(node, null, idx);
                }

                idx++;
                flattenResolveJayway(ctx, n1, result);
            }
        } else {
            if (ctx.getMode() == QueryMode.CREATE) {
                node.asObject();
            }

            if (expression.test(node, ctx)) {
                result.add(node);
            }
        }
    }

    private void flattenResolveIetf(QueryContext ctx, ONode node, List<ONode> result) {
        if (ctx.getMode() == QueryMode.CREATE) {
            node.asObject();
        }

        if (node.isArray()) {
            int idx = 0;
            for (ONode n1 : node.getArray()) {
                if (n1.source == null) {
                    n1.source = new PathSource(node, null, idx);
                }

                idx++;
                if (expression.test(n1, ctx)) {
                    result.add(n1);
                }
            }
        } else if (node.isObject()) {
            for (Map.Entry<String, ONode> entry : node.getObject().entrySet()) {
                ONode n1 = entry.getValue();

                if (n1.source == null) {
                    n1.source = new PathSource(node, entry.getKey(), 0);
                }

                if (expression.test(n1, ctx)) {
                    result.add(n1);
                }
            }
        } else {
            if (expression.test(node, ctx)) {
                result.add(node);
            }
        }
    }
}