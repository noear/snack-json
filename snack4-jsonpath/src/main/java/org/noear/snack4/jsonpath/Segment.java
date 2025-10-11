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

/**
 * JsonPath 片段
 *
 * @author noear
 * @since 4.0
 * */
public interface Segment {
    /**
     * 是否为多出
     */
    boolean isMultiple();

    /**
     * 分析
     *
     * @param currentNodes 当前节点
     * @param ctx          查询上下文
     */
    List<ONode> resolve(QueryContext ctx, List<ONode> currentNodes);
}