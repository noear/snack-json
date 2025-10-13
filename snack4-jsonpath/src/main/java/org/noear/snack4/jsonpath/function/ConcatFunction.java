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

import java.util.List;

/**
 *
 * @author noear 2025/10/12 created
 * @since 4.0
 */
public class ConcatFunction implements Function {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> currentNodes, List<ONode> argNodes) {
        ONode arg0 = null;
        ONode arg1 = null;

        if (ctx.isInFilter()) {
            if (argNodes.size() != 2) {
                throw new JsonPathException("Requires 2 parameters");
            }

            arg0 = argNodes.get(0);
            arg1 = argNodes.get(1);
        } else {
            if (argNodes.size() != 1) {
                throw new JsonPathException("Requires 1 parameter");
            }
            arg0 = new ONode(currentNodes);
            arg1 = argNodes.get(0);
        }

        if (arg0.isArray()) {
            List<ONode> oNodes = arg0.getArray();

            if (ctx.hasFeature(Feature.JsonPath_JaywayMode)) {
                if (oNodes.size() > 0) {
                    for(ONode n1: oNodes) {
                        if (n1.isString()) {
                            n1.setValue(n1.toString().concat(arg1.toString()));
                        } else {
                            n1.setValue(arg1.toString());
                        }
                    }
                }

                if(ctx.isMultiple()) {
                    return arg0;
                } else {
                    return arg0.get(0);
                }
            } else {
                if (oNodes.size() > 1) {
                    return arg0.add(arg1);
                } else {
                    ONode n1 = oNodes.get(0);
                    if (n1.isArray()) {
                        return n1.add(arg1);
                    } else if (n1.isString()) {
                        return new ONode(n1.getString().concat(arg1.getString()));
                    }
                }
            }
        }

        return ctx.newNode();
    }
}