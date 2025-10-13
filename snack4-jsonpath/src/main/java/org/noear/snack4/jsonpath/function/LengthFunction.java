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
package org.noear.snack4.jsonpath.function;

import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.Function;
import org.noear.snack4.jsonpath.JsonPathException;
import org.noear.snack4.jsonpath.QueryContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串、数组或对象的长度
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public class LengthFunction implements Function {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> currentNodes, List<ONode> argNodes) {
        if (ctx.isInFilter()) {
            if (argNodes.size() != 1) {
                throw new JsonPathException("Requires 1 parameters");
            }

            ONode n = argNodes.get(0);
            return lengthOf(ctx, n);
        } else {

            if (ctx.hasFeature(Feature.JsonPath_JaywayMode)) {
                List<ONode> results = new ArrayList<>();

                for (ONode n1 : currentNodes) {
                    if (n1.isArray()) {
                        results.add(ctx.newNode(n1.getArray().size()));
                    } else {
                        results.add(ctx.newNode());
                    }
                }

                if (results.size() > 0) {
                    if (ctx.isMultiple()) {
                        return ctx.newNode(results);
                    } else {
                        return results.get(0);
                    }
                } else {
                    throw new JsonPathException("Aggregation function attempted to calculate value using empty array");
                }
            } else {
                if (currentNodes.size() > 0) {
                    if (currentNodes.size() > 1) {
                        return ctx.newNode(currentNodes.size());
                    } else {
                        ONode n = currentNodes.get(0);
                        return lengthOf(ctx, n);
                    }
                }
            }

            return ctx.newNode();
        }
    }

    private ONode lengthOf(QueryContext ctx, ONode n) {
        if (n.isString()) return ctx.newNode(n.getString().length());
        if (n.isArray()) return ctx.newNode(n.size());
        if (n.isObject()) return ctx.newNode(n.getObject().size());

        return ctx.newNode();
    }
}
