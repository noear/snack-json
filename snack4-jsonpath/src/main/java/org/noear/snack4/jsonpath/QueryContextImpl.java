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

import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.jsonpath.segment.DescendantSegment;
import org.noear.snack4.jsonpath.segment.FuncSegment;
import org.noear.snack4.jsonpath.segment.Segment;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 查询上下文实现
 *
 * @author noear
 * @since 4.0
 * */
public class QueryContextImpl implements QueryContext {
    private final ONode root;
    private final QueryMode mode;
    private final Options options;

    private boolean multiple;
    private boolean expanded;
    private boolean descendant;

    public QueryContextImpl(ONode root, QueryMode mode) {
        this.root = root;
        this.mode = mode;

        if (root != null) {
            this.options = root.options();
        } else {
            this.options = Options.DEF_OPTIONS;
        }
    }

    /**
     * 跟踪（执行之后）
     */
    protected void tailafter(Segment seg) {
        if (seg instanceof DescendantSegment) {
            //后代片段
            descendant = true;
        }

        if (seg instanceof FuncSegment) {
            //函数片段
            multiple = false;
        } else {
            //其它片段
            multiple = multiple || seg.isMultiple();
        }

        expanded = expanded || seg.isExpanded();
    }

    @Override
    public boolean hasFeature(Feature feature) {
        return options.hasFeature(feature);
    }

    @Override
    public boolean isMultiple() {
        return multiple;
    }

    @Override
    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public boolean isDescendant() {
        return descendant;
    }

    @Override
    public ONode getRoot() {
        return root;
    }

    @Override
    public QueryMode getMode() {
        return mode;
    }

    @Override
    public Options getOptions() {
        return options;
    }

    private Map<String, Object> attach;

    private Map<String, Object> getAttach() {
        if (attach == null) {
            attach = new HashMap<>();
        }

        return attach;
    }

    public <T> T cacheIfAbsent(String key, Function<String, ?> mappingFunction) {
        return (T) getAttach().computeIfAbsent(key, mappingFunction);
    }

    /**
     * 内嵌查询
     */
    public QueryResult nestedQuery(ONode target, JsonPath query) {
        if (query.isRooted()) {
            return cacheIfAbsent(query.getExpression(), k -> query.select(getRoot()));
        }

        if (getMode() == QueryMode.CREATE) {
            return query.create(target);
        } else {
            return query.select(target);
        }
    }
}