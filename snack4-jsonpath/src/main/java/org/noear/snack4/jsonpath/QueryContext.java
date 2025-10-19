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

import java.util.function.Function;

/**
 * 查询上下文
 *
 * @author noear
 * @since 4.0
 * */
public interface QueryContext {
    /**
     * 有使用标准？
     */
    boolean hasFeature(Feature feature);

    /**
     * Jayway 兼容模式
     */
    boolean forJaywayMode();

    /**
     * 在过滤器中的
     */
    boolean isFiltered();

    /**
     * 是否多输出（前面执行过 `..x` 或 `*` 或 `[?]`）
     */
    boolean isMultiple();

    /**
     * 是否已展开（前面执行过 `..x` 或 `*`）
     */
    boolean isExpanded();

    /**
     * 是否有后代选择（前面执行过 `..x`）
     */
    boolean isDescendant();

    /**
     * 查询根节点
     */
    ONode getRoot();

    /**
     * 查询模式
     */
    QueryMode getMode();

    /**
     * 获取根选项配置
     */
    Options getOptions();

    /**
     * 缓存获取
     */
    <T> T cacheIfAbsent(String key, Function<String, ?> mappingFunction);

    /**
     * 内嵌查询（`@.user.name`）
     */
    QueryResult nestedQuery(ONode target, JsonPath query);

    /**
     * 新建节点
     */
    default ONode newNode() {
        return new ONode(getOptions());
    }

    /**
     * 新建节点
     */
    default ONode newNode(Object value) {
        return new ONode(getOptions(), value);
    }
}