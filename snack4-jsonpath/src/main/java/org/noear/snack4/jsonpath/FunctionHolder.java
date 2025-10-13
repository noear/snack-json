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
package org.noear.snack4.jsonpath;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.util.SelectUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public class FunctionHolder {
    public final String funcName;
    public final Function func;
    public final List<Object> args;

    @Override
    public String toString() {
        return funcName;
    }

    public FunctionHolder(String description) {
        int bl = description.indexOf('(');

        this.funcName = description.substring(0, bl);
        String argsStr0 = description.substring(bl + 1, description.length() - 1);
        List<String> argsStr = SelectUtil.splitSelectors(argsStr0);

        this.func = FunctionLib.get(funcName);

        Objects.requireNonNull(func, "The function not found: " + funcName);

        this.args = new ArrayList<>();

        for (String arg : argsStr) {
            if (arg.length() > 0) {
                char ch = arg.charAt(0);
                if (ch == '@' || ch == '$') {
                    //查询
                    args.add(JsonPath.parse(arg));
                } else if (ch == '/') {
                    //正则
                    args.add(new ONode(arg));
                } else {
                    //字符串或数字或json
                    args.add(ONode.ofJson(arg));
                }
            }
        }
    }

    public ONode apply(QueryContext ctx, List<ONode> currentNodes) {
        if (args.isEmpty()) {
            return func.apply(ctx, currentNodes, Collections.emptyList());
        } else {
            List<ONode> argNodes = new ArrayList<>();

            for (Object arg : args) {
                argNodes.add((ONode) arg);
            }

            return func.apply(ctx, currentNodes, argNodes);
        }
    }

    public ONode apply(QueryContext ctx, ONode node) {
        List<ONode> argNodes = new ArrayList<>();

        for (Object arg : args) {
            if (arg instanceof JsonPath) {
                List<ONode> currentNodes = ctx.nestedQuery(node, (JsonPath) arg).getNodeList();
                argNodes.add(ctx.newNode(currentNodes));
            } else {
                argNodes.add((ONode) arg);
            }
        }

        QueryContextImpl ctx0 = (QueryContextImpl) ctx;

        try {
            ctx0.setInFilter(true);
            return func.apply(ctx, null, argNodes);
        } finally {
            ctx0.setInFilter(false);
        }
    }
}