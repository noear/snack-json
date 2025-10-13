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
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.util.IndexUtil;
import org.noear.snack4.jsonpath.util.SelectUtil;

import java.util.List;

/**
 * 名称选择器：选择对象的命名子对象（如 $.demo, $['demo']）
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public class NameSelector implements Selector {
    private final String expr;

    private String name;

    public NameSelector(String expr) {
        this.expr = expr;

        this.name = expr;
        if (expr.length() > 2) {
            char ch = expr.charAt(0);
            if (ch == '\'' || ch == '"') {
                this.name = expr.substring(1, expr.length() - 1);
            }
        }
    }

    @Override
    public String toString() {
        return "'" + expr + "'";
    }

    @Override
    public boolean isMultiple() {
        return false;
    }

    @Override
    public void select(QueryContext ctx, boolean isDescendant, List<ONode> currentNodes, List<ONode> results) {
        if (isDescendant) {
            boolean forJayway = ctx.hasFeature(Feature.JsonPath_Jayway);

            //后裔
            SelectUtil.descendantSelect(currentNodes, !forJayway, (n1) -> {
                IndexUtil.forKey(ctx, n1, name, results);
            });
        } else {
            for (ONode n : currentNodes) {
                IndexUtil.forKey(ctx, n, name, results);
            }
        }
    }
}