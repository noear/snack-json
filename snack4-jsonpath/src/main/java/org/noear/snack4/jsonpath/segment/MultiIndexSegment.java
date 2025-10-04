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
import org.noear.snack4.exception.PathResolutionException;
import org.noear.snack4.json.JsonSource;
import org.noear.snack4.jsonpath.Context;
import org.noear.snack4.jsonpath.QueryMode;
import org.noear.snack4.jsonpath.SegmentFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 处理多索引选择（如 [1,4], ['a','b']）
 *
 * @author noear 2025/10/3 created
 */
public class MultiIndexSegment implements SegmentFunction {
    private boolean isAll;
    private List<String> keys;
    private List<Integer> indices;

    public MultiIndexSegment(String segmentStr) {
        if (segmentStr.indexOf('*') >= 0) {
            //通配符
            isAll = true;
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
    public List<ONode> resolve(List<ONode> currentNodes, Context context, QueryMode mode) {
        List<ONode> result = new ArrayList<>();

        for (ONode n : currentNodes) {
            if (isAll) {
                if (n.isArray()) {
                    result.addAll(n.getArray());
                } else if (n.isObject()) {
                    for (Map.Entry<String, ONode> entry : n.getObject().entrySet()) {
                        result.add(entry.getValue());
                    }
                }
            } else if (keys != null) {
                for (String k : keys) {
                    if (n.isObject()) {
                        ONode p = n.get(k);
                        if (p != null) {
                            result.add(p);
                        }
                    }
                }
            } else {
                for (Integer idx : indices) {
                    if (n.isArray()) {
                        if (idx < 0) idx += n.size();
                        if (idx < 0 || idx >= n.size()) {
                            throw new PathResolutionException("Index out of bounds: " + idx);
                        }
                        ONode node = n.get(idx);
                        node.source = new JsonSource(n, null, idx);
                        result.add(node);
                    }
                }
            }

        }

        return result;
    }
}