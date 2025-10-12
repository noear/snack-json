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
import org.noear.snack4.jsonpath.JsonPath;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.func.Func;
import org.noear.snack4.jsonpath.func.FuncHolder;
import org.noear.snack4.jsonpath.func.FuncLib;
import org.noear.snack4.jsonpath.util.SelectUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 聚合函数（如 $.list.last() ）
 *
 * @author noear
 * @since 4.0
 */
public class FuncSegment extends AbstractSegment {
    private final String description;
    private final FuncHolder funcHolder;

    public FuncSegment(String description) {
        this.description = description;

        this.funcHolder = new FuncHolder(description);
    }

    @Override
    public String toString() {
        return description;
    }

    @Override
    public boolean isMultiple() {
        return false;
    }

    @Override
    public List<ONode> resolve(QueryContext ctx, List<ONode> currentNodes) {
        if (isDescendant()) {
            List<ONode> results = new ArrayList<>();
            SelectUtil.descendantSelect(currentNodes, results::add);
            currentNodes = results;
        }

        if (currentNodes.isEmpty()) {
            return currentNodes;
        }

        return Collections.singletonList(
                apply(ctx, currentNodes) // 传入节点列表
        );
    }

    private ONode apply(QueryContext ctx, List<ONode> currentNodes) {
        return funcHolder.apply(ctx, currentNodes);
    }
}