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
public class EmptyOperator implements Operator {
    @Override
    public boolean apply(QueryContext ctx, ONode node, Term term) {
        ONode leftNode = term.getLeftNode(ctx, node);
        ONode rightNode = term.getRightNode(ctx, node);

        if (rightNode.isBoolean() == false) {
            return false;
        }

        return leftNode.isNullOrEmpty() && rightNode.getBoolean();
    }
}
