package org.noear.snack4.jsonpath.util;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.PathSource;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public class SelectUtil {

    public static void descendantSelect(List<ONode> currentNodes, List<ONode> results, Predicate<ONode> tester) {
        for (ONode node : currentNodes) {
            collectRecursive(node, results, tester);
        }
    }

    private static void collectRecursive(ONode node, List<ONode> results, Predicate<ONode> tester) {
        if (node.isArray()) {
            int idx = 0;
            for (ONode n1 : node.getArray()) {
                if (n1.source == null) {
                    n1.source = new PathSource(node, null, idx);
                }

                idx++;

                if (tester.test(n1)) {
                    results.add(n1);
                }

                collectRecursive(n1, results, tester);
            }
        } else if (node.isObject()) {
            for (Map.Entry<String, ONode> entry : node.getObject().entrySet()) {
                ONode n1 = entry.getValue();
                if (n1.source == null) {
                    n1.source = new PathSource(node, entry.getKey(), 0);
                }

                if (tester.test(n1)) {
                    results.add(n1);
                }

                collectRecursive(n1, results, tester);
            }
        }
    }
}