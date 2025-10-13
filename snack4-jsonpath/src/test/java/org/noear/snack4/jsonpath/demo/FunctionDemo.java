package org.noear.snack4.jsonpath.demo;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.FunctionLib;
import org.noear.snack4.jsonpath.JsonPathException;

/**
 *
 * @author noear 2025/10/11 created
 */
public class FunctionDemo {
    public static void main(String[] args) {
        //定制聚合函数
        FunctionLib.register("parent", (ctx, currentNodes, argNodes) -> {
            if (currentNodes.size() == 1) {
                ONode node = currentNodes.get(0);
                if (node.parent() == null) {
                    return node;
                } else {
                    return node.parent();
                }
            } else {
                throw new JsonPathException("Invalid currentNodes");
            }
        });

        //检验效果（在 IETF 规范里以子项进行过滤，即 1,2） //out: [1,2]
        System.out.println(ONode.ofJson("{'a':1,'b':2}")
                .select("$[?@.parent().a == 1]")
                .toJson());

        //参考 //out: [1]
        System.out.println(ONode.ofJson("{'a':1,'b':2}")
                .select("$[?@ == 1]")
                .toJson());
    }
}