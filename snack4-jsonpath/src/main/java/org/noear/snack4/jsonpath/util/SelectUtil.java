package org.noear.snack4.jsonpath.util;

import org.noear.snack4.ONode;
import org.noear.snack4.node.PathSource;

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

    public static void descendantSelect(List<ONode> currentNodes, boolean andSelf, Consumer<ONode> consumer) {
        for (ONode node : currentNodes) {
            if (andSelf) {
                consumer.accept(node);
            }

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
     * 按顶层逗号分割选择器字符串，会忽略括号、方括号、花括号以及引号内的逗号。
     *
     * @param segmentStr 待分割的字符串，例如 "0, 'name', ?(@.price < 10 && @.category in ['books', 'fiction']), {'a':'c', 'b': 'd'}"
     * @return 分割后的选择器列表
     */
    public static List<String> splitSelectors(String segmentStr) {
        List<String> result = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        int parenLevel = 0;     // 圆括号 () 的嵌套层级
        int bracketLevel = 0;   // 方括号 [] 的嵌套层级
        int curlyBraceLevel = 0;// 花括号 {} 的嵌套层级 (新增)

        boolean inQuote = false; // 是否在引号内部
        char quoteChar = 0;      // 当前的引号类型 (' 或 ")

        for (int i = 0, len = segmentStr.length(); i < len; i++) {
            char ch = segmentStr.charAt(i);

            // 1. 处理引号状态
            if (ch == '\'' || ch == '\"') {
                if (inQuote) {
                    // 引号关闭：当前字符与起始引号匹配
                    if (ch == quoteChar) {
                        inQuote = false;
                        quoteChar = 0;
                    }
                } else {
                    // 引号开启
                    inQuote = true;
                    quoteChar = ch;
                }
            }

            // 如果在引号内部，则不进行任何平衡计数，直接追加并继续
            if (inQuote) {
                currentChunk.append(ch);
                continue;
            }


            // 2. 检查顶层逗号分割
            // 只有当逗号在最外层，且所有平衡计数器都为 0 时，才进行分割
            if (ch == ',' && parenLevel == 0 && bracketLevel == 0 && curlyBraceLevel == 0) {
                result.add(currentChunk.toString().trim());
                currentChunk.setLength(0); // 重置 StringBuilder
                continue; // 跳过当前逗号
            }

            // 3. 更新平衡计数层级
            if (ch == '(') {
                parenLevel++;
            } else if (ch == ')') {
                parenLevel--;
            } else if (ch == '[') {
                bracketLevel++;
            } else if (ch == ']') {
                bracketLevel--;
            } else if (ch == '{') { // 新增花括号处理
                curlyBraceLevel++;
            } else if (ch == '}') { // 新增花括号处理
                curlyBraceLevel--;
            }

            // 4. 追加字符
            currentChunk.append(ch);
        }

        // 检查平衡性（可选，但推荐用于错误报告）
        if (parenLevel != 0 || bracketLevel != 0 || curlyBraceLevel != 0) {
            // 在实际解析器中，这里应该抛出 JsonPathException
            //throw new JsonPathException("Unbalanced brackets/parentheses in selector: " + segmentStr);
            // 这里我们仅继续执行，因为您只要求优化分割逻辑
        }

        // 添加最后一个片段
        String lastChunk = currentChunk.toString().trim();
        if (!lastChunk.isEmpty() || result.isEmpty()) {
            result.add(lastChunk);
        }

        // 移除因空输入或末尾逗号导致的空字符串（例如 "a,b," -> [a, b, ""] 这种情况）
        result.removeIf(String::isEmpty);

        return result;
    }
}