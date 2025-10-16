package demo.snack4.jsonpath;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.FunctionLib;
import org.noear.snack4.jsonpath.JsonPathException;
import org.noear.snack4.jsonpath.util.MathUtil;

import java.util.List;

/**
 *
 * @author noear 2025/10/11 created
 */
public class FunctionDemo {
    public static void main(String[] args) {
        //定制 sum 函数
        FunctionLib.register("sum", (ctx, argNodes) -> {
            if (argNodes.size() != 1) {
                throw new JsonPathException("Requires 1 parameters");
            }

            ONode arg0 = argNodes.get(0); //节点列表（选择器的结果）

            if (arg0.getArray().size() > 0) {
                List<Double> doubleList = MathUtil.getDoubleList(ctx, arg0);

                if(doubleList.size() > 0){
                    double ref = 0;
                    for (Double d : doubleList) {
                        ref += d;
                    }

                    return ctx.newNode(ref);
                }
            }

            return ctx.newNode();
        });

        //检验效果//out: 6.0
        System.out.println(ONode.ofJson("[1,2,3]")
                .select("$.sum()")
                .toJson());
    }
}