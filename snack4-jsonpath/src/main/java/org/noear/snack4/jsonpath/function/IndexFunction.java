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
import org.noear.snack4.jsonpath.QueryMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回当前数组中索引为X的元素。X可以是负数（从末尾开始计算）
 *
 * @author noear 2025/10/12 created
 * @since 4.0
 */
public class IndexFunction implements Function {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> currentNodes, List<ONode> argNodes) {
        ONode arg1 = null;

        if (ctx.isInFilter()) {
            if (argNodes.size() != 2) {
                throw new JsonPathException("Requires 2 parameters");
            }

            currentNodes = argNodes.get(0).getArray();
            arg1 = argNodes.get(1);
        } else {
            if (argNodes.size() != 1) {
                throw new JsonPathException("Requires 1 parameter");
            }

            arg1 = argNodes.get(0);
        }

        if (arg1.isNumber() == false) {
            throw new JsonPathException("Requires arg1 is number");
        }

        int index = arg1.getInt();

        if (currentNodes.isEmpty()) {
            if (ctx.getMode() == QueryMode.CREATE) {
                currentNodes.add(ctx.newNode().getOrNew(index));
            } else {
                return ctx.newNode();
            }
        }

        if (ctx.hasFeature(Feature.JsonPath_JaywayMode)) {
            List<ONode> results = new ArrayList<>();

            if (currentNodes.size() > 0) {
                for (ONode n1 : currentNodes) {
                    if (n1.isArray()) {
                        ONode tmp = n1.get(index);
                        if (tmp.isNull() == false) {
                            results.add(tmp);
                        }
                    }
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
            if (currentNodes.size() > 1) {
                if (index < 0) {
                    index = index + currentNodes.size();
                }

                if (index < currentNodes.size()) {
                    return currentNodes.get(index);
                } else {
                    return ctx.newNode();
                }
            } else {
                ONode n1 = currentNodes.get(0);
                if (n1.isArray()) {
                    return n1.get(index);
                }
            }
        }

        return ctx.newNode();
    }
}