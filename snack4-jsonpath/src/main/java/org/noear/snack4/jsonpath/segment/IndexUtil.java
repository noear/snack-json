package org.noear.snack4.jsonpath.segment;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.PathSource;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.QueryMode;

import java.util.List;

/**
 *
 * @author noear 2025/10/9 created
 *
 */
public class IndexUtil {
    public static void forKey(QueryContext ctx, ONode node, String key, List<ONode> result) {
        if (ctx.getMode() == QueryMode.CREATE) {
            node.asObject();
        }

        if (node.isObject() == false) {
            return;
        }

        forKeyUnsafe(ctx, node, key, result);
    }

    public static void forKeyUnsafe(QueryContext ctx, ONode node, String key, List<ONode> result) {
        ONode n1 = ctx.getNodeBy(node, key);

        if (n1 != null) {
            if (n1.source == null) {
                n1.source = new PathSource(node, key, 0);
            }

            result.add(n1);
        }
    }

    public static void forIndex(QueryContext ctx, ONode node, int idx, List<ONode> result) {
        if (ctx.getMode() == QueryMode.CREATE) {
            node.asArray();
        }

        if (node.isArray() == false) {
            return;
        }

        forIndexUnsafe(ctx, node, idx, result);
    }

    public static void forIndexUnsafe(QueryContext ctx, ONode node, int idx, List<ONode> result) {
        if (idx < 0) {
            idx += node.size();
        }

        ONode n1 = ctx.getNodeAt(node, idx);

        if (n1 != null) {
            if (n1.source == null) {
                n1.source = new PathSource(node, null, idx);
            }

            result.add(n1);
        }
    }
}