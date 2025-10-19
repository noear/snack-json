package demo.snack4.jsonpath;

import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.FunctionLib;
import org.noear.snack4.jsonpath.JsonPathException;

/**
 *
 * @author noear 2025/10/11 created
 */
public class FunctionDemo {
    public static void main(String[] args) {
        //定制 length 函数（已预置）
        FunctionLib.register("length", (ctx, argNodes) -> {
            if (argNodes.size() != 1) {
                throw new JsonPathException("Requires 1 parameters");
            }

            ONode arg0 = argNodes.get(0); //节点列表（选择器的结果）

            if (ctx.isMultiple()) {
                return ctx.newNode(arg0.getArray().size());
            } else {
                if (arg0.getArray().size() > 0) {
                    ONode n1 = arg0.get(0);

                    if (n1.isArray()) return ctx.newNode(n1.getArray().size());
                    if (n1.isObject()) return ctx.newNode(n1.getObject().size());

                    if (ctx.hasFeature(Feature.JsonPath_JaywayMode) == false) {
                        if (n1.isString()) return ctx.newNode(n1.getString().length());
                    }
                }

                //不出异常，兼容 jayway
                return ctx.newNode();
            }
        });

        //检验效果//out: 3
        System.out.println(ONode.ofJson("[1,2,3]")
                .select("$.length()")
                .toJson());
    }
}