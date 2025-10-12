package org.noear.snack4.jsonpath.func;

import org.noear.snack4.ONode;
import org.noear.snack4.Standard;
import org.noear.snack4.jsonpath.Func;
import org.noear.snack4.jsonpath.QueryContext;

import java.util.List;

/**
 *
 * @author noear 2025/10/12 created
 *
 */
public class MaxFunc implements Func {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> oNodes) {
        if (oNodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        boolean isJayway = ctx.hasStandard(Standard.JSONPath_Jayway);

        Double ref = null;
        for (ONode n : oNodes) {
            if (n.isArray()) {
                for (ONode o : n.getArray()) {
                    if (o.isNumber()) {
                        if (ref == null) {
                            ref = o.getDouble();
                        } else {
                            if (ref < o.getDouble()) {
                                ref = o.getDouble();
                            }
                        }
                    }
                }
            } else if (n.isNumber()) {
                if (isJayway == false) {
                    if (ref == null) {
                        ref = n.getDouble();
                    } else {
                        if (ref < n.getDouble()) {
                            ref = n.getDouble();
                        }
                    }
                }
            }
        }

        return new ONode(ctx.getOptions(), ref);
    }
}
