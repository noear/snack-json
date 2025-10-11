package org.noear.snack4.jsonpath.demo;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.OperationLib;

/**
 *
 * @author noear 2025/10/11 created
 *
 */
public class OperationDemo {
    public static void main(String[] args) {
        OperationLib.register("startsWith", (ctx, node, condition) -> {
            ONode leftNode = condition.getLeftNode(ctx, node);

            if (leftNode.isString()) {
                ONode rightNode = condition.getRightNode(ctx, node);
                if (rightNode.isNull()) {
                    return false;
                }

                return leftNode.getString().startsWith(rightNode.getString());
            }
            return false;
        });

        assert ONode.ofJson("{'list':['a','b','c']}")
                .select("$.list[?@ startsWith 'a']")
                .size() == 1;
    }
}
