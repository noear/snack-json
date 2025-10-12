package org.noear.snack4.jsonpath.demo;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.OperatorLib;

/**
 *
 * @author noear 2025/10/11 created
 */
public class OperationDemo {
    public static void main(String[] args) {
        //定制操作符
        OperatorLib.register("startsWith", (ctx, node, term) -> {
            ONode leftNode = term.getLeftNode(ctx, node);

            if (leftNode.isString()) {
                ONode rightNode = term.getRightNode(ctx, node);
                if (rightNode.isNull()) {
                    return false;
                }

                return leftNode.getString().startsWith(rightNode.getString());
            }
            return false;
        });

        //检验效果
        assert ONode.ofJson("{'list':['a','b','c']}")
                .select("$.list[?@ startsWith 'a']")
                .size() == 1;
    }
}
