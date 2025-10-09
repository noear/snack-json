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
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.QueryMode;
import org.noear.snack4.jsonpath.Segment;

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
    public List<ONode> resolve(List<ONode> currentNodes, QueryContext context) {
        List<ONode> result = new ArrayList<>();

        for (ONode n : currentNodes) {
            if (isAll) {
                if (n.isArray()) {
                    int idx = 0;
                    for(ONode n1 : n.getArray()) {
                        if(n1.source == null) {
                            n1.source = new PathSource(n, null, idx);
                        }

                        result.add(n1);
                        idx++;
                    }
                } else if (n.isObject()) {
                    for (Map.Entry<String, ONode> entry : n.getObject().entrySet()) {
                        ONode n1 =  entry.getValue();
                        if(n1.source == null) {
                            n1.source = new PathSource(n, entry.getKey(), 0);
                        }

                        result.add(n1);
                    }
                }
            } else if (keys != null) {
                for (String k : keys) {
                    if (n.isObject()) {
                        ONode n1 = n.getOrNull(k);
                        if (n1 != null) {
                            if(n1.source == null) {
                                n1.source = new PathSource(n, k, 0);
                            }

                            result.add(n1);
                        }
                    }
                }
            } else {
                for (Integer idx : indices) {
                    if (n.isArray()) {
                        if (idx < 0) idx += n.size();
                        if (idx < 0 || idx >= n.size()) {
                            throw new JsonPathException("Index out of bounds: " + idx);
                        }
                        ONode n1 = n.getOrNull(idx);
                        if(n1.source == null) {
                            n1.source = new PathSource(n, null, idx);
                        }

                        result.add(n1);
                    }
                }
            }

        }

        return result;
    }
}