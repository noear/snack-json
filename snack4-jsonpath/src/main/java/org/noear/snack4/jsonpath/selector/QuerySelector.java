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

import org.noear.snack4.core.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.JsonPath;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.util.IndexUtil;
import org.noear.snack4.jsonpath.util.SelectUtil;

import java.util.List;

/**
 * 查询选择器
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public class QuerySelector implements Selector {
    private final String expr;
    private JsonPath jsonPath;

    public QuerySelector(String expr) {
        this.expr = expr;
        this.jsonPath = JsonPath.parse(expr);
    }

    @Override
    public String toString() {
        return expr;
    }

    @Override
    public boolean isMultiple() {
        return false;
    }

    @Override
    public boolean isExpanded() {
        return false;
    }

    @Override
    public void select(QueryContext ctx, boolean isDescendant, List<ONode> currentNodes, List<ONode> results) {
        if (isDescendant) {
            boolean forJayway = ctx.hasFeature(Feature.JsonPath_JaywayMode);

            //后代（IETF JSONPath (RFC 9535)：包括“自己”和“后代”）
            SelectUtil.descendantSelect(currentNodes, !forJayway, (n1) -> {
                resolve(ctx, n1, results);
            });
        } else {
            for (ONode n1 : currentNodes) {
                resolve(ctx, n1, results);
            }
        }
    }

    private void resolve(QueryContext ctx, ONode n1, List<ONode> results) {
        ONode dynamicIdx = ctx.nestedQuery(n1, jsonPath).reduce();

        if (dynamicIdx.isNumber()) {
            IndexUtil.forIndex(ctx, n1, dynamicIdx.getInt(), results);
        } else if (dynamicIdx.isString()) {
            IndexUtil.forKey(ctx, n1, dynamicIdx.getString(), results);
        }
    }
}