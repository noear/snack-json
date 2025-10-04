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
import org.noear.snack4.jsonpath.QueryMode;
import org.noear.snack4.jsonpath.SegmentFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 处理递归搜索 ..
 *
 * @author noear 2025/10/3 created
 */
public class RecursiveSegment implements SegmentFunction {
    @Override
    public List<ONode> resolve(List<ONode> currentNodes, Context context, QueryMode mode) {
        List<ONode> result = new ArrayList<>();

        for (ONode node : currentNodes) {
            collectRecursive(node, result);
        }

        return result;
    }

    private void collectRecursive(ONode node, List<ONode> results) {
        if (node.isArray()) {
            for (ONode n1 : node.getArray()) {
                results.add(n1);
                collectRecursive(n1, results);
            }
        } else if (node.isObject()) {
            for (Map.Entry<String, ONode> entry : node.getObject().entrySet()) {
                results.add(entry.getValue());
                collectRecursive(entry.getValue(), results);
            }
        }
    }
}
