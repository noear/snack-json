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

import java.util.List;

/**
 *
 * @author noear 2025/10/13 created
 * @since 4.0
 */
public class QueryResult {
    private final QueryContext ctx;
    private final List<ONode> nodeList;

    public QueryResult(QueryContext ctx, List<ONode> nodeList) {
        this.ctx = ctx;
        this.nodeList = nodeList;
    }

    public QueryContext getContext() {
        return ctx;
    }

    /**
     * 节点列表
     *
     */
    public List<ONode> getNodeList() {
        return nodeList;
    }

    /**
     * 转为节点（相当于 Flux -> Mono）
     *
     */
    public ONode asNode() {
        if (ctx.hasFeature(Feature.JsonPath_AlwaysReturnList)) {
            return ctx.newNode(nodeList);
        } else if (ctx.hasFeature(Feature.JsonPath_AsPathList)) {
            return ctx.newNode().addAll(ctx.newNode(nodeList).pathList());
        } else {
            return reduce();
        }
    }

    /**
     * 收缩节点
     */
    public ONode reduce() {
        if (nodeList.size() > 1) {
            return ctx.newNode(nodeList);
        } else {
            if (ctx.isMultiple()) {
                return ctx.newNode(nodeList);
            } else {
                if (nodeList.size() > 0) {
                    return nodeList.get(0);
                } else {
                    return ctx.newNode();
                }
            }
        }
    }
}