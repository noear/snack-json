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
import org.noear.snack4.jsonpath.util.RegexUtil;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 正则表达式完全匹配
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public class MatchFunction implements Function {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> currentNodes, List<ONode> argNodes) {
        if (argNodes.size() != 2) {
            throw new JsonPathException("Requires 2 parameters");
        }

        ONode o1 = argNodes.get(0);

        if (o1.isNull()) {
            return ctx.newNode(false);
        }

        String arg0 = o1.toString();
        String arg1 = argNodes.get(1).toString();

        Pattern pattern = RegexUtil.parse(arg1);
        boolean found = pattern.matcher(arg0).matches(); //与 SearchFunc 的区别就在这儿

        return ctx.newNode(found);
    }
}
