package org.noear.snack4.jsonpath.func;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.Func;
import org.noear.snack4.jsonpath.QueryContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author noear 2025/10/12 created
 *
 */
public class KeysFunc implements Func {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> oNodes) {
        if (oNodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        if (oNodes.size() > 1) {
            Set<String> results = new HashSet<>();
            for (ONode n1 : oNodes) {
                if (n1.isObject() && n1.getObject().size() > 0) {
                    results.addAll(n1.getObject().keySet());
                }
            }

            if (results.size() > 0) {
                return new ONode(ctx.getOptions()).addAll(results);
            }
        } else {
            ONode n1 = oNodes.get(0);

            if (n1.isObject() && n1.getObject().size() > 0) {
                return ONode.ofBean(n1.getObject().keySet());
            }
        }

        return new ONode(ctx.getOptions());
    }
}
