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

/**
 *
 * @author noear 2025/10/12 created
 *
 */
public class SubsetofOperator implements Operator {
    @Override
    public boolean apply(QueryContext ctx, ONode node, Term term) {
        ONode leftNode = term.getLeftNode(ctx, node);
        ONode rightNode = term.getRightNode(ctx, node);

        if (rightNode.isArray() == false || leftNode.isArray() == false) {
            return false;
        }

        return doSubsetof(leftNode, rightNode);
    }

    private boolean doSubsetof(ONode leftNode, ONode rightNode) {
        boolean rst = true;

        if (leftNode.isArray()) {
            if (leftNode.getArray().size() == 0) {
                return true; //(空集是任何集合的子集)
            }

            for (ONode ref : leftNode.getArray()) {
                rst &= MatchUtil.isValueMatch(rightNode, ref);
            }
        } else {
            rst &= MatchUtil.isValueMatch(rightNode, leftNode);
        }

        return rst;
    }
}