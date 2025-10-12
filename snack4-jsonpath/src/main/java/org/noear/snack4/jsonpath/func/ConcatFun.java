package org.noear.snack4.jsonpath.func;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.JsonPathException;
import org.noear.snack4.jsonpath.QueryContext;

import java.util.List;

/**
 *
 * @author noear 2025/10/12 created
 *
 */
public class ConcatFun implements  Func {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> args) {
        if (args.size() != 2) {
            throw new JsonPathException("Requires 2 parameters");
        }

        ONode arg0 = args.get(0);
        ONode arg1 = args.get(1);

        if (arg0.isArray()) {
            List<ONode> oNodes = arg0.getArray();

            if (oNodes.size() > 1) {
                return arg0.add(arg1);
            } else {
                ONode n1 = oNodes.get(0);
                if (n1.isArray()) {
                    return n1.add(arg1);
                } else if (n1.isString()) {
                    return new ONode(n1.getString().concat(arg1.getString()));
                }
            }
        }

        return new ONode(null);
    }
}
