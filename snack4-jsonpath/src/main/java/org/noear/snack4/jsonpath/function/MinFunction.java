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
import org.noear.snack4.jsonpath.util.MathUtil;
import org.noear.snack4.util.Asserts;

import java.util.List;

/**
 *
 * @author noear 2025/10/12 created
 * @since 4.0
 */
public class MinFunction implements Function {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> currentNodes, List<ONode> argNodes) {
        if (currentNodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        List<Double> doubleList = null;

        if (ctx.hasFeature(Feature.JsonPath_Jayway)) {
            if (ctx.isDescendant()) {
                doubleList = MathUtil.getDoubleList(currentNodes);
            } else {
                doubleList = MathUtil.getDoubleListByChild(currentNodes);
            }

            if (Asserts.isEmpty(doubleList)) {
                throw new JsonPathException("Aggregation function attempted to calculate value using empty array");
            }
        } else {
            doubleList = MathUtil.getDoubleList(currentNodes);

            if (Asserts.isEmpty(doubleList)) {
                return new ONode(ctx.getOptions());
            }
        }


        double ref = doubleList.get(0);
        for (double d : doubleList) {
            if (ref > d) {
                ref = d;
            }
        }

        return new ONode(ctx.getOptions(), ref);
    }
}
