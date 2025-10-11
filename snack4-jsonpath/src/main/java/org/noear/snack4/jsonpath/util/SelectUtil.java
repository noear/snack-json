package org.noear.snack4.jsonpath.util;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.PathSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public class SelectUtil {

    public static void descendantSelect(List<ONode> currentNodes, Consumer<ONode> consumer) {
        for (ONode node : currentNodes) {
            collectRecursive(node, consumer);
        }
    }

    private static void collectRecursive(ONode node, Consumer<ONode> consumer) {
        if (node.isArray()) {
            int idx = 0;
            for (ONode n1 : node.getArray()) {
                if (n1.source == null) {
                    n1.source = new PathSource(node, null, idx);
                }

                idx++;

                consumer.accept(n1);
                collectRecursive(n1, consumer);
            }
        } else if (node.isObject()) {
            for (Map.Entry<String, ONode> entry : node.getObject().entrySet()) {
                ONode n1 = entry.getValue();
                if (n1.source == null) {
                    n1.source = new PathSource(node, entry.getKey(), 0);
                }

                consumer.accept(n1);
                collectRecursive(n1, consumer);
            }
        }
    }

    /**
     * 按顶层逗号分割选择器字符串，会忽略括号和方括号内的逗号。
     *
     * @param segmentStr 待分割的字符串，例如 "0, 'name', ?(@.price < 10 && @.category in ['books', 'fiction'])"
     * @return 分割后的选择器列表
     */
    public static List<String> splitSelectors(String segmentStr) {
        List<String> result = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        int parenLevel = 0;   // 圆括号 () 的嵌套层级
        int bracketLevel = 0; // 方括号 [] 的嵌套层级

        for (int i = 0, len = segmentStr.length(); i < len; i++) {
            char ch = segmentStr.charAt(i);

            if (ch == ',' && parenLevel == 0 && bracketLevel == 0) {
                // 只有当逗号在最外层时，才进行分割
                result.add(currentChunk.toString().trim());
                currentChunk.setLength(0); // 重置 StringBuilder
            } else {
                // 更新嵌套层级
                if (ch == '(') {
                    parenLevel++;
                } else if (ch == ')') {
                    parenLevel--;
                } else if (ch == '[') {
                    bracketLevel++;
                } else if (ch == ']') {
                    bracketLevel--;
                }
                currentChunk.append(ch);
            }
        }
        // 添加最后一个片段
        result.add(currentChunk.toString().trim());

        return result;
    }
}