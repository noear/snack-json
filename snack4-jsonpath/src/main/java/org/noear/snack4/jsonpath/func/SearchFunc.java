package org.noear.snack4.jsonpath.func;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.JsonPathException;
import org.noear.snack4.jsonpath.QueryContext;

import java.util.List;

/**
 *
 * @author noear 2025/10/11 created
 */
public class SearchFunc implements Func {
    @Override
    public ONode apply(QueryContext ctx, List<ONode> oNodes) {
        if (oNodes.size() != 2) {
            throw new JsonPathException("The parameter requires two");
        }

        String arg0 = oNodes.get(0).toString();
        String arg1 = oNodes.get(1).toString();

        boolean found = arg0.indexOf(arg1) >= 0;

        return new ONode(found);
    }
}
