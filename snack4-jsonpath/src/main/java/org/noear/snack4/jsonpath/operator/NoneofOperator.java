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
public class NoneofOperator implements Operator {
    @Override
    public boolean apply(QueryContext ctx, ONode node, Term term) {
        ONode leftNode = term.getLeftNode(ctx, node);
        ONode rightNode = term.getRightNode(ctx, node);

        if (rightNode.isArray() == false || leftNode.isArray() == false) {
            return false;
        }

        return !applyDo(ctx, leftNode, rightNode);
    }

    boolean applyDo(QueryContext ctx, ONode leftNode, ONode rightNode) {
        boolean rst = true;

        if (leftNode.isArray()) {
            if (leftNode.size() == 0) {
                rst = false;
            } else {
                for (ONode ref : leftNode.getArray()) {
                    rst &= MatchUtil.isValueMatch(rightNode, ref);
                }
            }
        } else {
            rst &= MatchUtil.isValueMatch(rightNode, leftNode);
        }

        return rst;
    }
}