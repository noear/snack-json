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
package org.noear.snack4.jsonpath.operator;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.Operator;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.filter.Term;
import org.noear.snack4.jsonpath.util.JsRegexUtil;

/**
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public class MatchesOperator implements Operator {
    @Override
    public boolean apply(QueryContext ctx, ONode node, Term term) {
        ONode leftNode = term.getLeftNode(ctx, node);
        ONode rightNode = term.getRightNode(ctx, node);

        boolean found = false;
        if (leftNode.isValue()) {
            if (rightNode.isString()) {
                String v = rightNode.getString();//.replace("\\/", "/");
                found = JsRegexUtil.of(v).matcher(leftNode.getString()).matches(); //不能用 find
            }
        }

        return found;
    }
}
