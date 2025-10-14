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
 *
 * @author noear 2025/10/12 created
 * @since 4.0
 */
public class AppendFunction implements Function {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> argNodes) {
        if (argNodes.size() != 2) {
            throw new JsonPathException("Requires 2 parameters");
        }

        ONode arg0 = argNodes.get(0); //节点列表（选择器的结果）
        ONode arg1 = argNodes.get(1);

        if (ctx.isMultiple()) {
            for (ONode n1 : arg0.getArray()) {
                if (n1.isArray()) {
                    n1.add(arg1);
                }
            }

            return arg0;
        } else {
            if (arg0.isEmpty()) {
                return ctx.newNode();
            } else {
                ONode n1 = arg0.get(0);
                if (n1.isArray()) {
                    n1.add(arg1);
                }

                return n1;
            }
        }
    }
}