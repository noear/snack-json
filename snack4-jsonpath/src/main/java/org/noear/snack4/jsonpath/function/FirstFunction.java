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
 * 返回当前数组的第一个元素
 *
 * @author noear 2025/10/12 created
 * @since 4.0
 */
public class FirstFunction implements Function {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> currentNodes, List<ONode> argNodes) {
        if (ctx.isInFilter()) {
            if (argNodes.size() != 1) {
                throw new JsonPathException("Requires 1 parameters");
            }

            ONode arg0 = argNodes.get(0);
            if (arg0.isArray() == false) {
                throw new JsonPathException("Requires 1 array parameters");
            }

            currentNodes = arg0.getArray();
        }

        if (currentNodes.isEmpty()) {
            return ctx.newNode();
        }

        if (ctx.hasFeature(Feature.JsonPath_JaywayMode)) {
            List<ONode> results = new ArrayList<>();

            for (ONode n1 : currentNodes) {
                if (n1.isArray()) {
                    results.add(n1.get(0));
                }
            }

            if (results.size() > 0) {
                if (results.size() == 1) {
                    return results.get(0);
                } else {
                    return ctx.newNode(results);
                }
            } else {
                throw new JsonPathException("Aggregation function attempted to calculate value using empty array");
            }
        } else {
            if (currentNodes.size() > 1) {
                return currentNodes.get(0);
            } else {
                ONode n1 = currentNodes.get(0);
                if (n1.isArray()) {
                    return n1.get(0);
                } else {
                    return n1;
                }
            }
        }
    }
}
