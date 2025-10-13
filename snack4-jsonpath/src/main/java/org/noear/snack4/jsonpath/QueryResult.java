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

import java.util.List;

/**
 *
 * @author noear 2025/10/13 created
 * @since 4.0
 */
public class QueryResult {
    private final QueryContext ctx;
    private final List<ONode> currentNodes;

    public QueryResult(QueryContext ctx, List<ONode> currentNodes) {
        this.ctx = ctx;
        this.currentNodes = currentNodes;
    }

    public QueryContext getContext() {
        return ctx;
    }

    public List<ONode> getCurrentNodes() {
        return currentNodes;
    }

    public ONode asNode() {
        if (ctx.hasFeature(Feature.JsonPath_AlwaysReturnList)) {
            return ctx.newNode(currentNodes);
        } else {
            return autoNode();
        }
    }

    public ONode autoNode() {
        if (currentNodes.size() > 1) {
            return ctx.newNode(currentNodes);
        } else {
            if (ctx.isMultiple()) {
                return ctx.newNode(currentNodes);
            } else {
                if (currentNodes.size() > 0) {
                    return currentNodes.get(0);
                } else {
                    return ctx.newNode();
                }
            }
        }
    }
}