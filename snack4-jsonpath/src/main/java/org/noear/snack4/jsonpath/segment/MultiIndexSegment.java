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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 处理多索引选择（如 $.list[1,4], ['a','b']）
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public class MultiIndexSegment implements Segment {
    private final String segmentStr;
    private boolean isWildcard; //是否为通配符？
    private List<String> keys;
    private List<Integer> indices;

    public MultiIndexSegment(String segmentStr) {
        this.segmentStr = segmentStr;

        if (segmentStr.indexOf('*') >= 0) {
            //通配符
            isWildcard = true;
        } else if (segmentStr.indexOf('\'') >= 0) {
            //key
            this.keys = Arrays.stream(segmentStr.split(","))
                    .map(String::trim)
                    .map(k -> k.substring(1, k.length() - 1))
                    .collect(Collectors.toList());
        } else {
            //index
            this.indices = Arrays.stream(segmentStr.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public String toString() {
        return "[" + segmentStr + "]";
    }

    @Override
    public List<ONode> resolve(QueryContext ctx, List<ONode> currentNodes) {
        List<ONode> result = new ArrayList<>();

        for (ONode n : currentNodes) {
            doResolve(ctx, n, result);
        }

        return result;
    }

    private void doResolve(QueryContext ctx, ONode node, List<ONode> result) {
        if (isWildcard) { //本级偏平化
            if (node.isArray()) {
                int idx = 0;
                for (ONode n1 : node.getArray()) {
                    if (n1.source == null) {
                        n1.source = new PathSource(node, null, idx);
                    }

                    idx++;
                    result.add(n1);
                }
            } else if (node.isObject()) {
                for (Map.Entry<String, ONode> entry : node.getObject().entrySet()) {
                    ONode n1 = entry.getValue();
                    if (n1.source == null) {
                        n1.source = new PathSource(node, entry.getKey(), 0);
                    }

                    result.add(n1);
                }
            }
        } else if (keys != null) {
            if (ctx.getMode() == QueryMode.CREATE) {
                node.asObject();
            }

            if (node.isObject()) {
                for (String key : keys) {
                    ONode n1 = null;
                    if (ctx.getMode() == QueryMode.CREATE) {
                        n1 = node.getOrNew(key);
                    } else {
                        n1 = node.getOrNull(key);
                    }

                    if (n1 != null) {
                        if (n1.source == null) {
                            n1.source = new PathSource(node, key, 0);
                        }

                        result.add(n1);
                    }
                }
            }
        } else {
            if (ctx.getMode() == QueryMode.CREATE) {
                node.asArray();
            }

            if (node.isArray()) {
                for (Integer idx : indices) {
                    if (idx < 0) {
                        idx += node.size();
                    }

                    if (idx < 0 || idx >= node.size()) {
                        throw new JsonPathException("Index out of bounds: " + idx);
                    }

                    ONode n1 = null;
                    if (ctx.getMode() == QueryMode.CREATE) {
                        n1 = node.getOrNew(idx);
                    } else {
                        n1 = node.getOrNull(idx);
                    }

                    if (n1 != null) {
                        if (n1.source == null) {
                            n1.source = new PathSource(node, null, idx);
                        }

                        result.add(n1);
                    }
                }
            }
        }
    }
}