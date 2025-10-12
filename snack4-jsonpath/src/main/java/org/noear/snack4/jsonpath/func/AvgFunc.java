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
public class AvgFunc implements Func {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> oNodes) {
        if (oNodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        Double ref = null;
        if (ctx.hasStandard(Standard.JSONPath_Jayway)) {
            ref = MathUtil.avgByChild(oNodes);
        } else {
            ref = MathUtil.avg(oNodes);
        }

        return new ONode(ctx.getOptions(), ref);
    }
}
