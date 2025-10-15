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

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.Function;
import org.noear.snack4.jsonpath.JsonPathException;
import org.noear.snack4.jsonpath.QueryContext;

import java.util.List;

/**
 * 节点列表的大小
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public class CountFunction implements Function {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> argNodes) {
        if (argNodes.size() != 1) {
            throw new JsonPathException("Requires 1 parameters");
        }
        ONode arg0 = argNodes.get(0); //节点列表（选择器的结果）

        if (ctx.isMultiple()) {
            return ctx.newNode(arg0.getArray().size());
        } else {
            if (arg0.getArray().size() == 1) {
                ONode n1 = arg0.get(0);

                if (n1.isString()) return ctx.newNode(n1.getString().length());
                if (n1.isArray()) return ctx.newNode(n1.getArray().size());
                if (n1.isObject()) return ctx.newNode(n1.getObject().size());
            }
        }

        return ctx.newNode();
    }
}