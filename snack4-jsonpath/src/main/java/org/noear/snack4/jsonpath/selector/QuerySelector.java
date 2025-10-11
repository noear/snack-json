package org.noear.snack4.jsonpath.selector;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.JsonPath;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.Selector;
import org.noear.snack4.jsonpath.util.IndexUtil;

import java.util.List;

/**
 *
 * @author noear 2025/10/11 created
 *
 */
public class QuerySelector implements Selector {
    private final String expr;
    private JsonPath jsonPath;

    public QuerySelector(String expr) {
        this.expr = expr;
        this.jsonPath = JsonPath.compile(expr);
    }

    @Override
    public void select(QueryContext ctx, List<ONode> currentNodes, List<ONode> results) {
        for (ONode node : currentNodes) {
            ONode dynamicIdx = ctx.nestedQuery(node, jsonPath);

            if (dynamicIdx.isNumber()) {
                IndexUtil.forIndex(ctx, node, dynamicIdx.getInt(), results);
            } else if (dynamicIdx.isString()) {
                IndexUtil.forKey(ctx, node, dynamicIdx.getString(), results);
            }
        }
    }
}
