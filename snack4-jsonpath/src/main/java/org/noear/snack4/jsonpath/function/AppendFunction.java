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
import org.noear.snack4.jsonpath.QueryContext;

import java.util.List;

/**
 *
 * @author noear 2025/10/12 created
 * @since 4.0
 */
public class AppendFunction extends AbstractFunction implements Function {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> currentNodes, List<ONode> argNodes) {
        currentNodes = getNodeList(ctx, currentNodes, argNodes); //arg0
        ONode arg1 = getArgAt(ctx, argNodes, 1);

        if (ctx.hasFeature(Feature.JsonPath_JaywayMode)) {
            if (currentNodes.size() > 0) {
                for (ONode n1 : currentNodes) {
                    if (n1.isArray()) {
                        n1.add(arg1);
                    }
                }
            }

            if (ctx.isMultiple()) {
                return ctx.newNode(currentNodes);
            } else {
                return currentNodes.get(0);
            }
        } else {
            if (currentNodes.size() > 1) {
                currentNodes.add(arg1);
                return ctx.newNode(currentNodes);
            } else {
                ONode n1 = currentNodes.get(0);
                if (n1.isArray()) {
                    return n1.add(arg1);
                } else if (n1.isString()) {
                    return new ONode(n1.getString().concat(arg1.getString()));
                }
            }
        }

        return ctx.newNode();
    }
}