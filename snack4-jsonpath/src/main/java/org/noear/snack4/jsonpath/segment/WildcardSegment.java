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
import org.noear.snack4.json.JsonSource;
import org.noear.snack4.jsonpath.Context;
import org.noear.snack4.jsonpath.QueryMode;
import org.noear.snack4.jsonpath.SegmentFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 处理通配符 * （子级偏平化）
 *
 * @author noear 2025/10/3 created
 */
public class WildcardSegment implements SegmentFunction {
    private boolean flattened;

    public WildcardSegment(boolean flattened) {
        this.flattened = flattened;
    }

    @Override
    public List<ONode> resolve(List<ONode> currentNodes, Context context, QueryMode mode) {
        if (flattened) {
            return currentNodes;
        }

        List<ONode> result = new ArrayList<>();

        for (ONode n : currentNodes) {
            List<ONode> childs = new ArrayList<>();

            if (n.isArray()) {
                int idx= 0;
                for (ONode n1 : n.getArray()) {
                    if (n1.source == null) {
                        n1.source = new JsonSource(n, null, idx);
                    }

                    childs.add(n1);
                    idx++;
                }
            } else if (n.isObject()) {
                for (Map.Entry<String, ONode> entry : n.getObject().entrySet()) {
                    ONode n1 = entry.getValue();
                    if(n1.source == null) {
                        n1.source = new JsonSource(n, entry.getKey(), 0);
                    }

                    childs.add(n1);
                }
            }

            if (childs.size() > 0) {
                result.addAll(childs);
            }
        }

        return result;
    }
}