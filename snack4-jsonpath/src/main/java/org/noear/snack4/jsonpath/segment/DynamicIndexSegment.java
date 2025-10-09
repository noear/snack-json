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
import org.noear.snack4.jsonpath.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理动态索引（如 $.meta[$.type] ）
 *
 * @author noear 2025/10/8 created
 * @since 4.0
 */
public class DynamicIndexSegment implements Segment {
    private final String segmentStr;

    public DynamicIndexSegment(String segmentStr) {
        this.segmentStr = segmentStr;
    }

    @Override
    public String toString() {
        return "[" + segmentStr + "]";
    }

    @Override
    public List<ONode> resolve(QueryContext ctx, List<ONode> currentNodes) {
        List<ONode> results = new ArrayList<>();

        for (ONode node : currentNodes) {
            // 1. 在当前节点上执行动态路径查询
            ONode dynamicResult = Condition.resolveNestedPath(ctx, node, segmentStr);

            if (dynamicResult.isNumber()) {
                IndexUtil.forIndex(ctx, node, dynamicResult.getInt(), results);
            } else if (dynamicResult.isString()) {
                IndexUtil.forKey(ctx, node, dynamicResult.getString(), results);
            }
        }

        return results;
    }
}