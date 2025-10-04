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
import java.util.List;

/**
 * 处理精确索引（支持负数）
 *
 * @author noear 2025/10/3 created
 */
public class IndexSegment implements SegmentFunction {
    private String key;
    private int index;


    public IndexSegment(String segmentStr) {
        if (segmentStr.indexOf('\'') < 0) {
            index = Integer.parseInt(segmentStr);
        } else {
            key = segmentStr.substring(1, segmentStr.length() - 1);
        }
    }

    @Override
    public List<ONode> resolve(List<ONode> currentNodes, Context context, QueryMode mode) {
        List<ONode> result = new ArrayList<>();

        if (key != null) {
            forKey(currentNodes, mode, result);
        } else {
            forIndex(currentNodes, mode, result);
        }

        return result;
    }

    private void forKey(List<ONode> currentNodes, QueryMode mode, List<ONode> result) {
        currentNodes.stream()
                .filter(o -> {
                    if (mode == QueryMode.CREATE) {
                        o.newObject();
                        return true;
                    } else {
                        return o.isObject();
                    }
                })
                .map(obj -> {
                    if (mode == QueryMode.CREATE) {
                        obj.getOrNew(key);
                    }

                    ONode rst = obj.get(key);
                    rst.source = new JsonSource(obj, key, 0);

                    return rst;
                })
                .forEach(result::add);
    }

    private void forIndex(List<ONode> currentNodes, QueryMode mode, List<ONode> result) {
        currentNodes.stream()
                .filter(o -> {
                    if (mode == QueryMode.CREATE) {
                        o.newArray();
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
                            arr.add(new ONode());
                        }
                    }

                    if (idx < 0 || idx >= arr.size()) {
                        throw new PathResolutionException("Index out of bounds: " + idx);
                    }

                    ONode rst = arr.get(idx);
                    rst.source = new JsonSource(arr, null, idx);

                    return rst;
                })
                .forEach(result::add);
    }
}