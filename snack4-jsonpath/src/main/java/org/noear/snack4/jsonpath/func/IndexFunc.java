package org.noear.snack4.jsonpath.func;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.Func;
import org.noear.snack4.jsonpath.JsonPathException;
import org.noear.snack4.jsonpath.QueryContext;

import java.util.List;

/**
 *
 * @author noear 2025/10/12 created
 * @since 4.0
 */
public class IndexFunc implements Func {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> args) {
        if (args.size() != 2) {
            throw new JsonPathException("Requires 2 parameters");
        }

        ONode arg0 = args.get(0);
        ONode arg1 = args.get(1);

        if (arg1.isNumber() == false) {
            throw new JsonPathException("Requires arg1 is number");
        }

        if (arg0.isArray()) {
            List<ONode> oNodes = arg0.getArray();

            if (oNodes.size() > 1) {
                return arg0.get(arg1.getInt());
            } else {
                ONode n1 = oNodes.get(0);
                if (n1.isArray()) {
                    return n1.get(arg1.getInt());
                }
            }
        }

        return new ONode(null);
    }
}
