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
 * @author noear 2025/10/14 created
 * @since 4.0
 */
public abstract class AbstractFunction implements Function {
    protected List<ONode> getNodeList(QueryContext ctx, List<ONode> currentNodes, List<ONode> argNodes) {
        if (ctx.isInFilter()) {
            if (argNodes.size() < 1) {
                throw new JsonPathException("Requires 1 parameters");
            }

            return argNodes.get(0).getArray();
        } else {
            return currentNodes;
        }
    }
}
