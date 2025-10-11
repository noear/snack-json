package org.noear.snack4.jsonpath.func;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.QueryContext;

import java.util.List;

/**
 *
 * @author noear 2025/10/11 created
 *
 */
public class ValueFunc implements Func {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> oNodes) {
        if (oNodes.size() > 0) {
            ONode n1 = oNodes.get(0);
            if (n1.isValue()) {
                return n1;
            } else if (n1.isArray()) {
                return n1.get(0);
            }
        }

        return new ONode(null);
    }
}
