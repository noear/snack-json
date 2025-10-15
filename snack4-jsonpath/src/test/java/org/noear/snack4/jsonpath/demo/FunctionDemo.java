package org.noear.snack4.jsonpath.demo;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.FunctionLib;

/**
 *
 * @author noear 2025/10/11 created
 */
public class FunctionDemo {
    public static void main(String[] args) {
        //定制 floor 函数
        FunctionLib.register("floor", (ctx, argNodes) -> {
            ONode arg0 = argNodes.get(0); //节点列表（选择器的结果）

            if (ctx.isMultiple()) {
                for (ONode n1 : arg0.getArray()) {
                    if (n1.isNumber()) {
                        n1.setValue(Math.floor(n1.getDouble()));
                    }
                }

                return arg0;
            } else {
                ONode n1 = arg0.get(0);

                if (n1.isNumber()) {
                    return ctx.newNode(Math.floor(n1.getDouble()));
                } else {
                    return ctx.newNode();
                }
            }
        });

        //检验效果（在 IETF 规范里以子项进行过滤，即 1,2） //out: 1.0
        System.out.println(ONode.ofJson("{'a':1,'b':2}")
                .select("$.a.floor()")
                .toJson());

        //参考 //out: 2.0
        System.out.println(ONode.ofJson("{'a':1,'b':2}")
                .select("$[?floor(@) > 1].first()")
                .toJson());
    }
}