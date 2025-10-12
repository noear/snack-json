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
public class AnyofOperator implements Operator {
    @Override
    public boolean apply(QueryContext ctx, ONode node, Term term) {
        ONode leftNode = term.getLeftNode(ctx, node);

        if (leftNode.isNull() == false) {
            ONode rightNode = term.getRightNode(ctx, node);
            if (rightNode.isArray() == false) {
                return false;
            }

            return rightNode.getArray().stream()
                    .anyMatch(v -> MatchUtil.isValueMatch(leftNode, v));
        }

        return false;
    }
}
