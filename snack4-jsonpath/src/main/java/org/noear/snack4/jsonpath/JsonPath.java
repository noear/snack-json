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
import org.noear.snack4.exception.PathResolutionException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JSON路径查询工具类
 */
public class JsonPath {
    private final String path;
    private final List<SegmentFunction> segments;
    private final boolean multiple;

    public JsonPath(String path, List<SegmentFunction> segments) {
        this.path = path;
        this.segments = segments;
        this.multiple = ((path.indexOf('?') < 0 && path.indexOf('*') < 0 && path.indexOf("..") < 0 && path.indexOf(",") < 0 && path.indexOf(":") < 0) || path.indexOf("()") > 0);
    }

    public ONode select(ONode root) {
        List<ONode> currentNodes = Collections.singletonList(root);
        Context context = new Context(root);
        for (SegmentFunction seg : segments) {
            currentNodes = seg.resolve(currentNodes, context, QueryMode.SELECT);
        }

        if (currentNodes.size() > 1) {
            return new ONode(currentNodes);
        } else {
            if (multiple) {
                if (currentNodes.size() > 0) {
                    return currentNodes.get(0);
                } else {
                    return new ONode();
                }
            } else {
                return new ONode(currentNodes);
            }
        }
    }

    public ONode create(ONode root) {
        List<ONode> currentNodes = Collections.singletonList(root);
        Context context = new Context(root);
        for (SegmentFunction seg : segments) {
            currentNodes = seg.resolve(currentNodes, context, QueryMode.CREATE);
        }

        if (currentNodes.size() > 1) {
            return new ONode(currentNodes);
        } else {
            if (multiple) {
                if (currentNodes.size() > 0) {
                    return currentNodes.get(0);
                } else {
                    return new ONode();
                }
            } else {
                return new ONode(currentNodes);
            }
        }
    }

    public void delete(ONode root) {
        List<ONode> currentNodes = Collections.singletonList(root);
        Context context = new Context(root);
        for (SegmentFunction seg : segments) {
            currentNodes = seg.resolve(currentNodes, context, QueryMode.DELETE);
        }

        if (currentNodes.size() == 1) {
            for (ONode n : currentNodes) {
                if (n.source != null) {
                    if (n.source.key != null) {
                        if ("*".equals(n.source.key)) {
                            n.source.parent.clear();
                        } else {
                            n.source.parent.remove(n.source.key);
                        }
                    } else {
                        n.source.parent.remove(n.source.index);
                    }
                }
            }
        }
    }

    /// //////////


    private static Map<String, JsonPath> cached = new ConcurrentHashMap<>();

    /**
     * 编译
     */
    public static JsonPath compile(String path) {
        if (!path.startsWith("$")) {
            throw new PathResolutionException("Path must start with $");
        }

        return cached.computeIfAbsent(path, JsonPathCompiler::compile);
    }

    /**
     * 根据 jsonpath 查询
     */
    public static ONode select(String json, String path) {
        return select(ONode.load(json), path);
    }

    /**
     * 根据 jsonpath 查询
     */
    public static ONode select(ONode root, String path) {
        return compile(path).select(root);
    }

    /**
     * 根据 jsonpath 生成
     */
    public static ONode create(ONode root, String path) {
        return compile(path).create(root);
    }

    /**
     * 根据 jsonpath 删除
     */
    public static void delete(ONode root, String path) {
        compile(path).delete(root);
    }
}