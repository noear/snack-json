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
import org.noear.snack4.jsonpath.selector.IndexSelector;
import org.noear.snack4.jsonpath.selector.NameSelector;
import org.noear.snack4.jsonpath.selector.QuerySelector;

import java.util.ArrayList;
import java.util.List;

/**
 * 索引选择器（如 $.list[1], $.list[-1], $.user['name'], $.user[$.label]）
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public class IndexSegment implements Segment {
    private final String segmentStr;
//    private JsonPath query;
//    private String key;
//    private int index;

    private Selector selector;

    public IndexSegment(String segmentStr) {
        this.segmentStr = segmentStr;

        if (segmentStr.startsWith("$.") || segmentStr.startsWith("@.")) {
            selector  =new QuerySelector(segmentStr);
           // query = JsonPath.compile(segmentStr);
        } else if (segmentStr.indexOf('\'') < 0) {
            selector = new IndexSelector(segmentStr);
            //index = Integer.parseInt(segmentStr);
        } else {
            selector = new NameSelector(segmentStr);
            //key = segmentStr.substring(1, segmentStr.length() - 1);
        }
    }

    @Override
    public String toString() {
        return "[" + segmentStr + "]";
    }

    @Override
    public List<ONode> resolve(QueryContext ctx, List<ONode> currentNodes) {
        List<ONode> result = new ArrayList<>();

        selector.select(ctx, currentNodes, result);

//        for (ONode node : currentNodes) {
//            if (query != null) {
//                //$.x
//                ONode dynamicIdx = ctx.nestedQuery(node, query);
//
//                if (dynamicIdx.isNumber()) {
//                    IndexUtil.forIndex(ctx, node, dynamicIdx.getInt(), result);
//                } else if (dynamicIdx.isString()) {
//                    IndexUtil.forKey(ctx, node, dynamicIdx.getString(), result);
//                }
//            } else if (key != null) {
//                //'name'
//                IndexUtil.forKey(ctx, node, key, result);
//            } else {
//                //idx
//                IndexUtil.forIndex(ctx, node, index, result);
//            }
//        }

        ctx.flattened = false;
        return result;
    }
}