package org.noear.snack4.jsonpath.filter;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.Function;
import org.noear.snack4.jsonpath.FunctionLib;
import org.noear.snack4.jsonpath.QueryContext;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author noear 2025/10/11 created
 *
 */
public class FunctionHolder implements Function {
    private String functionName;
    private Function function;
    private List<String> args;

    public FunctionHolder(String functionName, List<String> args) {
        this.functionName = functionName;
        this.function = FunctionLib.get(functionName);
        this.args = args;

        //Objects.requireNonNull(function, "The function not found: " + functionName);
    }

    @Override
    public ONode apply(QueryContext context, List<ONode> oNodes) {
        return function.apply(context, oNodes);
    }
}
