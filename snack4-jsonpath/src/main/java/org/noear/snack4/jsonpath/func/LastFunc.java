package org.noear.snack4.jsonpath.func;

import org.noear.snack4.ONode;
import org.noear.snack4.Standard;
import org.noear.snack4.jsonpath.Func;
import org.noear.snack4.jsonpath.JsonPathException;
import org.noear.snack4.jsonpath.QueryContext;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author noear 2025/10/12 created
 *
 */
public class LastFunc implements Func {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> oNodes) {
        if (oNodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        if (ctx.hasStandard(Standard.JSONPath_Jayway)) {
            List<ONode> results = new ArrayList<>();

            for (ONode n1 : oNodes) {
                if (n1.isArray()) {
                    results.add(n1.get(-1));
                }
            }

            if (results.size() > 0) {
                if (results.size() == 1) {
                    return results.get(0);
                } else {
                    return new ONode(ctx.getOptions(), results);
                }
            } else {
                throw new JsonPathException("Aggregation function attempted to calculate value using empty array");
            }
        } else {
            if (oNodes.size() > 1) {
                return oNodes.get(oNodes.size() - 1);
            } else {
                ONode n1 = oNodes.get(0);
                if (n1.isArray()) {
                    return n1.get(-1);
                } else {
                    return n1;
                }
            }
        }
    }
}