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
import java.util.List;

/**
 * 处理精确索引，支持负数反选（如 $.list[1], $.list[-1]）
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public class IndexSegment implements Segment {
    private final String segmentStr;
    private final String key;
    private final int index;

    public IndexSegment(String segmentStr) {
        this.segmentStr = segmentStr;

        if (segmentStr.indexOf('\'') < 0) {
            index = Integer.parseInt(segmentStr);
            key = null;
        } else {
            index = 0;
            key = segmentStr.substring(1, segmentStr.length() - 1);
        }
    }

    @Override
    public String toString() {
        return "[" + segmentStr + "]";
    }

    @Override
    public List<ONode> resolve(QueryContext ctx, List<ONode> currentNodes) {
        List<ONode> result = new ArrayList<>();

        for (ONode node : currentNodes) {
            if (key != null) {
                forKey(ctx, node, result);
            } else {
                forIndex(ctx, node, result);
            }
        }

        return result;
    }

    private void forKey(QueryContext ctx, ONode node, List<ONode> result) {
        if (ctx.getMode() == QueryMode.CREATE) {
            node.asObject();
        }

        if (node.isObject() == false) {
            return;
        }

        ONode n1 = ctx.getNodeBy(node, key);

        if (n1 != null) {
            if (n1.source == null) {
                n1.source = new PathSource(node, key, 0);
            }

            result.add(n1);
        }
    }

    private void forIndex(QueryContext ctx, ONode node, List<ONode> result) {
        if (ctx.getMode() == QueryMode.CREATE) {
            node.asArray();
        }

        if (node.isArray() == false) {
            return;
        }

        int idx = index;
        if (idx < 0) {
            idx += node.size();
        }

        ONode n1 = ctx.getNodeAt(node, idx);

        if (n1 != null) {
            if (n1.source == null) {
                n1.source = new PathSource(node, null, idx);
            }

            result.add(n1);
        }
    }
}