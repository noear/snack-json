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
import org.noear.snack4.Standard;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.util.IndexUtil;
import org.noear.snack4.jsonpath.util.SelectUtil;

import java.util.List;

/**
 * 索引选择器（如 $[1], $[-1]）
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public class IndexSelector implements Selector {
    private final String expr;

    private int index;

    public IndexSelector(String expr) {
        this.expr = expr;

        index = Integer.parseInt(expr);
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
    public void select(QueryContext ctx, boolean isDescendant, List<ONode> currentNodes, List<ONode> results) {
        if (isDescendant) {
            if(ctx.hasStandard(Standard.JSONPath_IETF_RFC_9535)){
                for (ONode node : currentNodes) {
                    IndexUtil.forIndex(ctx, node, index, results);
                }
            }
            //后裔
            SelectUtil.descendantSelect(currentNodes, (n1) -> {
                IndexUtil.forIndex(ctx, n1, index, results);
            });
        } else {
            for (ONode node : currentNodes) {
                IndexUtil.forIndex(ctx, node, index, results);
            }
        }
    }
}