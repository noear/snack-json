package org.noear.snack4.jsonpath.func;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.QueryContext;

import java.util.List;

/**
 *
 * @author noear 2025/10/11 created
 *
 */
public class CountFunc implements Func {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> oNodes) {
        return new ONode(oNodes.size());
    }
}
