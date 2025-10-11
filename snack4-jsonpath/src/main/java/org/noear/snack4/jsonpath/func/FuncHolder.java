package org.noear.snack4.jsonpath.func;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.JsonPath;
import org.noear.snack4.jsonpath.QueryContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public class FuncHolder {
    private final String funcName;
    private final Func func;
    private final List<Object> args;

    public FuncHolder(String funcName, List<String> argsStr) {
        this.funcName = funcName;
        this.func = FuncLib.get(funcName);

        Objects.requireNonNull(func, "The function not found: " + funcName);

        this.args = new ArrayList<>();
        for (String arg : argsStr) {
            if (arg.length() > 0) {
                char ch = arg.charAt(0);
                if (ch == '@' || ch == '$') {
                    //查询
                    args.add(JsonPath.compile(arg));
                } else if (ch == '/') {
                    //正则
                    args.add(new ONode(arg));
                } else {
                    //字符串或数字
                    args.add(ONode.ofJson(arg));
                }
            }
        }
    }

    public ONode apply(QueryContext ctx, ONode node) {
        List<ONode> nodes = new ArrayList<>();

        for (Object arg : args) {
            if (arg instanceof JsonPath) {
                ONode n1 = ctx.nestedQuery(node, (JsonPath) arg);
                nodes.add(n1);
            } else {
                nodes.add((ONode) arg);
            }
        }

        return func.apply(ctx, nodes);
    }
}
