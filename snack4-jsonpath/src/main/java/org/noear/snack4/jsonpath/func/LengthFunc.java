package org.noear.snack4.jsonpath.func;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.QueryContext;

import java.util.List;

/**
 *
 * @author noear 2025/10/11 created
 */
public class LengthFunc implements Func {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> oNodes) {
        if (oNodes.size() == 1) {
            ONode n = oNodes.get(0);
            if (n.isString()) return new ONode(ctx.getOptions(), n.getString().length());
            if (n.isArray()) return new ONode(ctx.getOptions(), n.size());
            if (n.isObject()) return new ONode(ctx.getOptions(), n.getObject().size());
        }
        return new ONode(ctx.getOptions(), 0);
    }
}
