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
 * 将一个项或集合和当前数组连接成一个新数组
 *
 * @author noear 2025/10/12 created
 * @since 4.0
 */
public class ConcatFunction implements Function {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> argNodes) {
        if (argNodes.size() != 2) {
            throw new JsonPathException("Requires 2 parameters");
        }

        ONode arg0 = argNodes.get(0); //节点列表（选择器的结果）
        ONode arg1 = argNodes.get(1);

        if (arg0.isEmpty()) {
            return ctx.newNode();
        }

        if (arg1.isArray()) {
            arg1 = arg1.get(0);
        }

        if (ctx.isMultiple()) {
            if (ctx.isExpanded()) {
                for (ONode n1 : arg0.getArray()) {
                    n1.setValue("");
                }
            } else {
                for (ONode n1 : arg0.getArray()) {
                    if (n1.isString()) {
                        n1.setValue(n1.getString().concat(arg1.getString()));
                    } else {
                        n1.setValue(arg1.getString());
                    }
                }
            }

            return arg0;
        } else {
            ONode n1 = arg0.get(0);

            if (n1.isString()) {
                n1 = ctx.newNode(n1.getString().concat(arg1.getString()));
            } else {
                n1.setValue(arg1.getString());
            }

            return n1;
        }
    }
}