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

import org.noear.snack4.node.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.segment.Segment;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JsonPath
 *
 * @author noear
 * @since 4.0
 */
public class JsonPath {
    private final String expression;
    private final List<Segment> segments;
    private final boolean rooted;

    public JsonPath(String expression, List<Segment> segments) {
        this.expression = expression;
        this.segments = segments;
        this.rooted = expression.charAt(0) == '$';
    }

    public boolean isRooted() {
        return rooted;
    }

    public String getExpression() {
        return expression;
    }

    public List<Segment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    @Override
    public String toString() {
        return "JsonPath{" +
                "path='" + expression + '\'' +
                ", segments=" + segments +
                '}';
    }

    /**
     * 执行
     */
    protected QueryResult evaluate(QueryContextImpl ctx, ONode root) {
        List<ONode> currentNodes = Collections.singletonList(root);

        for (Segment seg : segments) {
            currentNodes = seg.resolve(ctx, currentNodes);
            ctx.multipleOf(seg);
        }

        return new QueryResult(ctx, currentNodes);
    }

    public QueryResult select(ONode root) {
        QueryContextImpl ctx = new QueryContextImpl(root, QueryMode.SELECT);

        try {
            return evaluate(ctx, root);
        } catch (Throwable ex) {
            if (ctx.hasFeature(Feature.JsonPath_SuppressExceptions)) {
                return new QueryResult(ctx, Collections.emptyList());
            } else {
                throw ex;
            }
        }
    }

    public QueryResult create(ONode root) {
        QueryContextImpl ctx = new QueryContextImpl(root, QueryMode.CREATE);

        try {
            if (expression.contains("..")) {
                throw new JsonPathException("The create mode not support descendant selector");
            }

            return evaluate(ctx, root);
        } catch (Throwable ex) {
            if (ctx.hasFeature(Feature.JsonPath_SuppressExceptions)) {
                return new QueryResult(ctx, Collections.emptyList());
            } else {
                throw ex;
            }
        }
    }

    public void delete(ONode root) {
        QueryContextImpl ctx = new QueryContextImpl(root, QueryMode.DELETE);

        try {
            QueryResult result = evaluate(ctx, root);

            for (ONode n1 : result.getNodeList()) {
                if (n1.source != null) {
                    if (n1.source.key != null) {
                        if ("*".equals(n1.source.key)) {
                            n1.source.parent.clear();
                        } else {
                            n1.source.parent.remove(n1.source.key);
                        }
                    } else {
                        n1.source.parent.remove(n1.source.index);
                    }
                }
            }
        } catch (Throwable ex) {
            if (ctx.hasFeature(Feature.JsonPath_SuppressExceptions)) {
                //...
            } else {
                throw ex;
            }
        }
    }

    /// //////////


    private static Map<String, JsonPath> cached = new ConcurrentHashMap<>();

    /**
     * 解析
     */
    public static JsonPath parse(String path) {
        if (!path.startsWith("$") && !path.startsWith("@")) {
            throw new JsonPathException("Path must start with $");
        }

        return cached.computeIfAbsent(path, JsonPathParser::parse);
    }

    /**
     * 根据 jsonpath 查询
     */
    public static ONode select(String json, String path) {
        return select(ONode.ofJson(json), path);
    }

    /**
     * 根据 jsonpath 查询
     */
    public static ONode select(ONode root, String path) {
        return parse(path).select(root).asNode();
    }

    /**
     * 根据 jsonpath 生成
     */
    public static ONode create(ONode root, String path) {
        return parse(path).create(root).asNode();
    }

    /**
     * 根据 jsonpath 删除
     */
    public static void delete(ONode root, String path) {
        parse(path).delete(root);
    }
}