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
package org.noear.snack4.jsonpath.func;

import org.noear.snack4.ONode;
import org.noear.snack4.Standard;
import org.noear.snack4.jsonpath.Func;
import org.noear.snack4.jsonpath.JsonPathException;
import org.noear.snack4.jsonpath.QueryContext;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public class LengthFunc implements Func {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> currentNodes, List<ONode> argNodes) {

        if (ctx.hasStandard(Standard.JSONPath_Jayway)) {
            List<ONode> results = new ArrayList<>();

            for (ONode n1 : currentNodes) {
                if (n1.isArray()) {
                    results.add(new ONode(n1.getArray().size()));
                } else {
                    results.add(new ONode());
                }
            }

            if (results.size() > 0) {
                if (ctx.isMultiple()) {
                    return new ONode(results);
                } else {
                    return results.get(0);
                }
            } else {
                throw new JsonPathException("Aggregation function attempted to calculate value using empty array");
            }
        } else {
            if (currentNodes.size() > 0) {
                if (currentNodes.size() > 1) {
                    return new ONode(ctx.getOptions(), currentNodes.size());
                } else {
                    ONode n = currentNodes.get(0);
                    if (n.isString()) return new ONode(ctx.getOptions(), n.getString().length());
                    if (n.isArray()) return new ONode(ctx.getOptions(), n.size());
                    if (n.isObject()) return new ONode(ctx.getOptions(), n.getObject().size());
                }
            }
        }

        return new ONode(null);
    }
}
