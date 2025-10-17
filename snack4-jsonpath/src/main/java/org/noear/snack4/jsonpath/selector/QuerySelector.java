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
import org.noear.snack4.jsonpath.JsonPath;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.util.IndexUtil;
import org.noear.snack4.jsonpath.util.SelectUtil;

import java.util.List;
import java.util.function.Consumer;

/**
 * 查询选择器
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public class QuerySelector extends AbstractSelector {
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
            //后代（IETF JSONPath (RFC 9535)：包括“自己”和“后代”）
            SelectUtil.descendantSelect(currentNodes, !ctx.forJaywayMode(), (n1) -> {
                onNext(ctx, n1, results::add);
            });
        } else {
            for (ONode n1 : currentNodes) {
                onNext(ctx, n1, results::add);
            }
        }
    }

    @Override
    public void onNext(QueryContext ctx, ONode node, Consumer<ONode> acceptor) {
        doQuery(ctx, node, n1 -> {
            onComplete(ctx, n1, acceptor);
        });
    }

    private void doQuery(QueryContext ctx, ONode n1, Consumer<ONode> acceptor) {
        ONode dynamicIdx = ctx.nestedQuery(n1, jsonPath).reduce();

        if (dynamicIdx.isNumber()) {
            IndexUtil.forIndex(ctx, n1, dynamicIdx.getInt(), acceptor);
        } else if (dynamicIdx.isString()) {
            IndexUtil.forKey(ctx, n1, dynamicIdx.getString(), acceptor);
        }
    }
}