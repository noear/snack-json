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
import org.noear.snack4.Options;
import org.noear.snack4.Standard;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 查询上下文
 *
 * @author noear
 * @since 4.0
 * */
public class QueryContext {
    private final ONode root;
    private final QueryMode mode;
    private final Options options;
    private final boolean RFC9535;

    public QueryContext(ONode root, QueryMode mode) {
        this.root = root;
        this.mode = mode;

        if (root != null) {
            this.options = root.options();
        } else {
            this.options = Options.DEF_OPTIONS;
        }

        RFC9535 = options.hasStandard(Standard.JSONPath_IETF_RFC_9535);
    }

    public ONode getRoot() {
        return root;
    }

    public boolean isRFC9535() {
        return RFC9535;
    }

    public boolean hasStandard(Standard standard) {
        return options.hasStandard(standard);
    }

    public QueryMode getMode() {
        return mode;
    }

    public Options getOptions() {
        return options;
    }

    public ONode getNodeBy(ONode node, String key) {
        if (mode == QueryMode.CREATE) {
            return node.getOrNew(key);
        } else {
            return node.getOrNull(key);
        }
    }

    public ONode getNodeAt(ONode node, int idx) {
        if (mode == QueryMode.CREATE) {
            return node.getOrNew(idx);
        } else {
            return node.getOrNull(idx);
        }
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
    public ONode nestedQuery(ONode target, JsonPath query) {
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