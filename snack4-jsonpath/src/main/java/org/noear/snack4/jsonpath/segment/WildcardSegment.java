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
import org.noear.snack4.jsonpath.Segment;
import org.noear.snack4.jsonpath.selector.WildcardSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 通配符选择器：选择节点的所有子节点（如 $.list.*）
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public class WildcardSegment implements Segment {
    private WildcardSelector selector = new WildcardSelector();

    @Override
    public String toString() {
        return "[*]";
    }

    @Override
    public List<ONode> resolve(QueryContext ctx, List<ONode> currentNodes) {
        if (ctx.flattened) {
            ctx.flattened = false;
            return currentNodes;
        }

        List<ONode> result = new ArrayList<>();

        selector.select(ctx, currentNodes, result);

        return result;
    }
}