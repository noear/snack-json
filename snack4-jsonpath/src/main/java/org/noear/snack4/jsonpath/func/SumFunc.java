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
public class SumFunc implements Func {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> oNodes) {
        if (oNodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        Double ref = null;

        if (ctx.hasStandard(Standard.JSONPath_Jayway)) {
            ref = MathUtil.sumByChild(oNodes);
        } else {
            ref = MathUtil.sum(oNodes);
        }

        return new ONode(ctx.getOptions(), ref);
    }
}
