package org.noear.snack4.jsonpath.selector;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.Selector;
import org.noear.snack4.jsonpath.segment.IndexUtil;

import java.util.List;

/**
 * 名称选择器：选择对象的命名子对象（如 $.demo, $['demo']）
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public class NameSelector implements Selector {
    private final String expr;

    private String name;

    public NameSelector(String expr) {
        this.expr = expr;
        this.name = expr.substring(1, expr.length() - 1);
    }

    @Override
    public String toString() {
        return "'" + expr + "'";
    }

    @Override
    public void select(QueryContext ctx, List<ONode> currentNodes, List<ONode> results) {
        for (ONode n : currentNodes) {
            IndexUtil.forKey(ctx, n, name, results);
        }
    }
}