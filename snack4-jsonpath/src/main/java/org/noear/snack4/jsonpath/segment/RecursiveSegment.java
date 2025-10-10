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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 处理递归搜索（如 $..a ）
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public class RecursiveSegment implements Segment {
    @Override
    public String toString() {
        return "..[*]";
    }

    @Override
    public List<ONode> resolve(QueryContext ctx, List<ONode> currentNodes) {
        List<ONode> result = new ArrayList<>();

        for (ONode node : currentNodes) {
            collectRecursive(node, result);
        }

        return result;
    }

    private void collectRecursive(ONode node, List<ONode> results) {
        if (node.isArray()) {
            int idx = 0;
            for (ONode n1 : node.getArray()) {
                if(n1.source == null) {
                    n1.source = new PathSource(node, null, idx);
                }

                idx++;
                results.add(n1);
                collectRecursive(n1, results);
            }
        } else if (node.isObject()) {
            for (Map.Entry<String, ONode> entry : node.getObject().entrySet()) {
                ONode n1 = entry.getValue();
                if(n1.source == null) {
                    n1.source = new PathSource(node, entry.getKey(), 0);
                }

                results.add(n1);
                collectRecursive(n1, results);
            }
        }
    }
}
