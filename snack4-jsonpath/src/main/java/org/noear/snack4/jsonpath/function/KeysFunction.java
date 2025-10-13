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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author noear 2025/10/12 created
 * @since 4.0
 */
public class KeysFunction implements Function {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> currentNodes, List<ONode> argNodes) {
        if (currentNodes.isEmpty()) {
            return ctx.newNode();
        }

        if (ctx.hasFeature(Feature.JsonPath_JaywayMode)) {
            Set<String> keys = new LinkedHashSet<>();

            for (ONode n1 : currentNodes) {
                if(n1.isObject()){
                    keys = n1.getObject().keySet();
                }
            }

            if(keys.size() > 0){
                return new ONode().addAll(keys);
            }  else{
                throw new JsonPathException("Aggregation function attempted to calculate value using empty object");
            }
        } else {
            if (currentNodes.size() > 1) {
                Set<String> results = new HashSet<>();
                for (ONode n1 : currentNodes) {
                    if (n1.isObject() && n1.getObject().size() > 0) {
                        results.addAll(n1.getObject().keySet());
                    }
                }

                if (results.size() > 0) {
                    return ctx.newNode().addAll(results);
                }
            } else {
                ONode n1 = currentNodes.get(0);

                if (n1.isObject() && n1.getObject().size() > 0) {
                    return ONode.ofBean(n1.getObject().keySet());
                }
            }
        }

        return ctx.newNode();
    }
}
