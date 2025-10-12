package org.noear.snack4.jsonpath.func;

import org.noear.snack4.ONode;
import org.noear.snack4.Standard;
import org.noear.snack4.jsonpath.Func;
import org.noear.snack4.jsonpath.JsonPathException;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.util.Asserts;

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

        List<Double> doubleList = null;

        if (ctx.hasStandard(Standard.JSONPath_Jayway)) {
            doubleList = MathUtil.getDoubleListByChild(oNodes);

            if (Asserts.isEmpty(doubleList)) {
                throw new JsonPathException("Aggregation function attempted to calculate value using empty array");
            }
        } else {
            doubleList = MathUtil.getDoubleList(oNodes);

            if (Asserts.isEmpty(doubleList)) {
                return new ONode(ctx.getOptions());
            }
        }


        double ref = doubleList.get(0);
        for (double d : doubleList) {
            if (ref < d) {
                ref = d;
            }
        }

        return new ONode(ctx.getOptions(), ref);
    }
}
