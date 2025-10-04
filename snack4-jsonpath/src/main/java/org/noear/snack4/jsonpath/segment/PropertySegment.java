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
 * 处理属性获取
 *
 * @author noear
 */
public class PropertySegment implements SegmentFunction {
    private String key;

    public PropertySegment(String key) {
        this.key = key;
    }

    @Override
    public List<ONode> resolve(List<ONode> currentNodes, Context context, QueryMode mode) {
        List<ONode> result = new ArrayList<>();

        for (ONode n : currentNodes) {
            getChild(n, key, mode, result);
        }

        return result;
    }

    private void getChild(ONode node, String key, QueryMode mode, List<ONode> result) {
        if (mode == QueryMode.CREATE) {
            node.newObject();
        }

        if (node.isObject()) {
            ONode child = node.get(key);

            if (child == null) {
                if (mode == QueryMode.CREATE) {
                    child = new ONode();
                    node.set(key, child);
                } else if (false) {
                    throw new PathResolutionException("Missing key '" + key + "'");
                }
            } else {
                child.source = new JsonSource(node, key, 0);
            }

            if (child != null) {
                result.add(child);
            }
        }
    }
}
