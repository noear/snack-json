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
package org.noear.snack4.jsonpath.func;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.JsonPathException;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.util.JsRegexUtil;

import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public class SearchFunc implements Func {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> args) {
        if (args.size() != 2) {
            throw new JsonPathException("The parameter requires two");
        }

        ONode o1 = args.get(0);

        if(o1.isNull()) {
            return new ONode(false);
        }

        String arg0 = o1.toString();
        String arg1 = args.get(1).toString();

        Pattern pattern = JsRegexUtil.of(arg1);
        boolean found = pattern.matcher(arg0).find(); //与 MatchFunc 的区别就在这儿

        return new ONode(found);
    }
}
