package org.noear.snack4.jsonpath.demo;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.FuncLib;
import org.noear.snack4.jsonpath.JsonPathException;

/**
 *
 * @author noear 2025/10/11 created
 */
public class FunctionDemo {
    public static void main(String[] args) {
        //定制聚合函数
        FuncLib.register("keys", (context, oNodes) -> {
            if (oNodes.size() == 1) {
                ONode node = oNodes.get(0);

                if (node.isObject()) {
                    return ONode.ofBean(node.getObject().keySet());
                } else {
                    throw new JsonPathException("keys() requires object");
                }
            } else {
                throw new JsonPathException("keys() requires object");
            }
        });

        //检验效果
        assert ONode.ofJson("{'a':1,'b':2}")
                .select("$.keys()")
                .size() == 2;
    }
}
