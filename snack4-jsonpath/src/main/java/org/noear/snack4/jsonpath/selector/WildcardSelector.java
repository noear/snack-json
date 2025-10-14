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
package org.noear.snack4.jsonpath.selector;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.PathSource;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.util.SelectUtil;

import java.util.List;
import java.util.Map;

/**
 * 通配符选择器：选择节点的所有子项（如 $.*, $[*], $..*, $..[*]）
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public class WildcardSelector implements Selector {
    @Override
    public String toString() {
        return "*";
    }

    @Override
    public boolean isMultiple() {
        return true;
    }

    @Override
    public boolean isExpanded() {
        return true;
    }

    @Override
    public void select(QueryContext ctx, boolean isDescendant, List<ONode> currentNodes, List<ONode> results) {
        if (isDescendant) {
            //后代（IETF JSONPath (RFC 9535)：包括“自己”和“后代”）
            SelectUtil.descendantSelect(currentNodes, false, results::add);
        } else {
            for (ONode n : currentNodes) {
                if (n.isArray()) {
                    int idx = 0;
                    for (ONode n1 : n.getArray()) {
                        if (n1.source == null) {
                            n1.source = new PathSource(n, null, idx);
                        }

                        idx++;
                        results.add(n1);
                    }
                } else if (n.isObject()) {
                    for (Map.Entry<String, ONode> entry : n.getObject().entrySet()) {
                        ONode n1 = entry.getValue();
                        if (n1.source == null) {
                            n1.source = new PathSource(n, entry.getKey(), 0);
                        }

                        results.add(n1);
                    }
                }
            }
        }
    }
}