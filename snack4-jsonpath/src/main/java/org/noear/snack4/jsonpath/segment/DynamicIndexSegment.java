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
import org.noear.snack4.jsonpath.JsonPathException;
import org.noear.snack4.jsonpath.PathSource;
import org.noear.snack4.jsonpath.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author noear 2025/10/8 created
 * @since 4.0
 */
public class DynamicIndexSegment implements SegmentFunction {
    private final String dynamicPath;

    public DynamicIndexSegment(String dynamicPath) {
        this.dynamicPath = dynamicPath;
    }

    @Override
    public List<ONode> resolve(List<ONode> currentNodes, Context context, QueryMode mode) {
        List<ONode> results = new ArrayList<>();

        for (ONode node : currentNodes) {
            // 1. 在当前节点上执行动态路径查询
            ONode dynamicResult = Condition.resolveNestedPath(node, dynamicPath, context.root);

            if (dynamicResult.isNumber()) {
                forIndex(Arrays.asList(node), dynamicResult.getInt(), mode, results);
            } else if (dynamicResult.isString()) {
                forKey(Arrays.asList(node), dynamicResult.getString(), mode, results);
            }
        }

        return results;
    }

    private void forKey(List<ONode> currentNodes, String key, QueryMode mode, List<ONode> result) {
        currentNodes.stream()
                .filter(o -> {
                    if (mode == QueryMode.CREATE) {
                        o.asObject();
                        return true;
                    } else {
                        return o.isObject();
                    }
                })
                .map(obj -> {
                    if (mode == QueryMode.CREATE) {
                        obj.getOrNew(key);
                    }

                    ONode n1 = obj.getOrNull(key);
                    if (n1.source == null) {
                        n1.source = new PathSource(obj, key, 0);
                    }

                    return n1;
                })
                .forEach(result::add);
    }

    private void forIndex(List<ONode> currentNodes, int index, QueryMode mode, List<ONode> result) {
        currentNodes.stream()
                .filter(o -> {
                    if (mode == QueryMode.CREATE) {
                        o.asArray();
                        return true;
                    } else {
                        return o.isArray();
                    }
                })
                .map(arr -> {
                    int idx = index;
                    if (idx < 0) {
                        idx = arr.size() + idx;
                    }

                    if (mode == QueryMode.CREATE) {
                        int count = idx + 1 - arr.size();
                        for (int i = 0; i < count; i++) {
                            arr.add(new ONode(arr.options()));
                        }
                    }

                    if (idx < 0 || idx >= arr.size()) {
                        throw new JsonPathException("Index out of bounds: " + idx);
                    }

                    ONode n1 = arr.getOrNull(idx);
                    if (n1.source == null) {
                        n1.source = new PathSource(arr, null, idx);
                    }

                    return n1;
                })
                .forEach(result::add);
    }
}