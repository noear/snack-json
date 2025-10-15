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
package org.noear.snack4.jsonpath;

import org.noear.snack4.ONode;

import java.util.List;
import java.util.Map;

/**
 * 路径源
 *
 * @author noear 2025/3/16 created
 * @since 4.0
 */
public class PathSource {
    public final ONode parent;
    public final String key;
    public final int index;

    public PathSource(ONode parent, String key, int index) {
        this.parent = parent;
        this.key = key;
        this.index = index;
    }

    /**
     * 分析路径
     *
     * @param oNode 节点
     */
    public static void resolvePath(ONode oNode) {
        if (oNode.isArray()) {
            int idx = 0;
            for (ONode n1 : oNode.getArray()) {
                if (n1.source == null) {
                    n1.source = new PathSource(oNode, null, idx);
                }
                idx++;

                resolvePath(n1);
            }
        } else if (oNode.isObject()) {
            for (Map.Entry<String, ONode> kv : oNode.getObject().entrySet()) {
                ONode n1 = kv.getValue();
                if (n1.source == null) {
                    n1.source = new PathSource(oNode, kv.getKey(), 0);
                }

                resolvePath(n1);
            }
        }
    }

    /**
     * 提取路径
     */
    public static void extractPath(List<String> paths, ONode oNode) {
        String path = oNode.path();
        if (path != null) {
            paths.add(path);
        }

        if (oNode.isArray()) {
            for (ONode n1 : oNode.getArray()) {
                extractPath(paths, n1);
            }
        } else if (oNode.isObject()) {
            for (Map.Entry<String, ONode> kv : oNode.getObject().entrySet()) {
                extractPath(paths, kv.getValue());
            }
        }
    }
}
