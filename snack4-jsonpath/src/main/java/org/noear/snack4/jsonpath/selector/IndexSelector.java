package org.noear.snack4.jsonpath.selector;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.Selector;
import org.noear.snack4.jsonpath.segment.IndexUtil;

import java.util.List;

/**
 * 索引选择器（如 $[1], $[-1]）
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public class IndexSelector implements Selector {
    private final String expr;

    private int index;

    public IndexSelector(String expr) {
        this.expr = expr;

        index = Integer.parseInt(expr);
    }

    @Override
    public String toString() {
        return expr;
    }

    @Override
    public void select(QueryContext ctx, List<ONode> currentNodes, List<ONode> results) {
        for (ONode node : currentNodes) {
            IndexUtil.forIndex(ctx, node, index, results);
        }
    }
}
