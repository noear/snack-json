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
import java.util.List;

/**
 * 处理属性获取（如 $.demo, $.demo.user）
 *
 * @author noear
 * @since 4.0
 */
public class PropertySegment implements Segment {
    private String key;

    public PropertySegment(String key) {
        this.key = key;
    }

    @Override
    public List<ONode> resolve(List<ONode> currentNodes, QueryContext context) {
        List<ONode> result = new ArrayList<>();

        for (ONode n : currentNodes) {
            getChild(n, key, context, result);
        }

        return result;
    }

    private void getChild(ONode node, String key, QueryContext context, List<ONode> result) {
        ONode n1 = null;

        if (context.getMode() == QueryMode.CREATE) {
            node.asObject();
            if (node.isObject()) {
                n1 = node.getOrNew(key);
            }
        } else {
            if (node.isObject()) {
                n1 = node.getOrNull(key);
            }
        }

        if (n1 != null) {
            if (n1.source == null) {
                n1.source = new PathSource(node, key, 0);
            }

            result.add(n1);
        }
    }
}
