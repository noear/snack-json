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
import org.noear.snack4.jsonpath.selector.*;
import org.noear.snack4.util.Asserts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 多索引选择器（如 $.list[1,4], ['a','b']）
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public class MultiIndexSegment implements Segment {
    private final String segmentStr;
    private List<Selector> selectors = new ArrayList<>();

    public MultiIndexSegment(String segmentStr) {
        this.segmentStr = segmentStr;

        for (String s : segmentStr.split(",")) {
            String chunk = s.trim();

            if (chunk.length() > 0) {
                char ch = chunk.charAt(0);

                if (ch == '*') {
                    selectors.add(new WildcardSelector());
                    //chunks.add(Boolean.TRUE);
                } else if (ch == '$' || ch == '@') {
                    selectors.add(new QuerySelector(chunk));
                    //chunks.add(JsonPath.compile(chunk));
                } else if (ch == '\'') {
                    selectors.add(new NameSelector(chunk));
                    //chunks.add(chunk.substring(1, chunk.length() - 1));
                } else if (ch == '?') {
                    selectors.add(new FilterSelector(chunk));
                } else {
                    selectors.add(new IndexSelector(chunk));
                    //chunks.add(Integer.parseInt(chunk));
                }
            }
        }
    }

    @Override
    public String toString() {
        return "[" + segmentStr + "]";
    }

    @Override
    public List<ONode> resolve(QueryContext ctx, List<ONode> currentNodes) {
        List<ONode> result = new ArrayList<>();

       for (Selector selector : selectors) {
           selector.select(ctx, currentNodes, result);
       }

        ctx.flattened = false;
        return result;
    }

//    private void doResolve(QueryContext ctx, ONode node, List<ONode> result) {
//        for (Object c1 : chunks) {
//            if (c1 instanceof Boolean) {
//                //*
//                if (node.isArray()) {
//                    int idx = 0;
//                    for (ONode n1 : node.getArray()) {
//                        if (n1.source == null) {
//                            n1.source = new PathSource(node, null, idx);
//                        }
//
//                        idx++;
//                        result.add(n1);
//                    }
//                } else if (node.isObject()) {
//                    for (Map.Entry<String, ONode> entry : node.getObject().entrySet()) {
//                        ONode n1 = entry.getValue();
//                        if (n1.source == null) {
//                            n1.source = new PathSource(node, entry.getKey(), 0);
//                        }
//
//                        result.add(n1);
//                    }
//                }
//            } else if (c1 instanceof JsonPath) {
//                //$.x
//                ONode dynamicIdx = ctx.nestedQuery(node, (JsonPath) c1);
//
//                if (dynamicIdx.isNumber()) {
//                    IndexUtil.forIndex(ctx, node, dynamicIdx.getInt(), result);
//                } else if (dynamicIdx.isString()) {
//                    IndexUtil.forKey(ctx, node, dynamicIdx.getString(), result);
//                }
//            } else if (c1 instanceof String) {
//                //'name'
//                if (ctx.getMode() == QueryMode.CREATE) {
//                    node.asObject();
//                }
//
//                if (node.isObject()) {
//                    IndexUtil.forKeyUnsafe(ctx, node, (String) c1, result);
//                }
//            } else if (c1 instanceof Integer) {
//                //idx
//                if (ctx.getMode() == QueryMode.CREATE) {
//                    node.asArray();
//                }
//
//                if (node.isArray()) {
//                    IndexUtil.forIndexUnsafe(ctx, node, (Integer) c1, result);
//                }
//            }
//        }
//    }
}