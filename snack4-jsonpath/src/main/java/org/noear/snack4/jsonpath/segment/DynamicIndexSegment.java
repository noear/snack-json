package org.noear.snack4.jsonpath.segment;

import org.noear.snack4.ONode;
import org.noear.snack4.exception.PathResolutionException;
import org.noear.snack4.jsonpath.Context;
import org.noear.snack4.jsonpath.JsonPath;
import org.noear.snack4.jsonpath.QueryMode;
import org.noear.snack4.jsonpath.SegmentFunction;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author noear 2025/10/8 created
 *
 */
public class DynamicIndexSegment implements SegmentFunction {
    private final String dynamicPath;
    private JsonPath compiledPath;

    public DynamicIndexSegment(String dynamicPath) {
        // 移除 $ 或 @ 前缀 (如果适用)
        if (dynamicPath.startsWith("$.")) {
            this.dynamicPath = dynamicPath;
        } else if (dynamicPath.startsWith("@.")) {
            // 支持 @ 引用当前节点
            this.dynamicPath = "$"+dynamicPath.substring(1);
        } else {
            throw new PathResolutionException("Dynamic path must start with '$' or '@'");
        }

        // 编译嵌套的 JsonPath (不在此处编译，因为 JsonPath.compile 依赖静态缓存，
        // 且可能导致编译时循环依赖或在 ONode 上执行编译，所以通常在 resolve() 中按需编译或查询)
        // 更好的做法是在 resolve() 中使用 JsonPath.select()
    }

    @Override
    public List<ONode> resolve(List<ONode> currentNodes, Context context, QueryMode mode) {
        List<ONode> results = new ArrayList<>();
        // 仅处理 SELECT 模式，CREATE/DELETE 可能复杂
        if (mode != QueryMode.SELECT) {
            throw new UnsupportedOperationException("Dynamic path in CREATE/DELETE is not supported");
        }

        for (ONode node : currentNodes) {
            // 1. 在当前节点上执行动态路径查询
            ONode dynamicResult = JsonPath.select(node, dynamicPath);

            // 2. 获取查询结果作为键/索引
            String key = dynamicResult.getString();
            if (key != null && !key.isEmpty()) {
                // 3. 使用结果作为键/索引进行下一步查询
                ONode target = node.get(key);
                if (target != null && target.isNull() == false) {
                    results.add(target);
                }
            }
        }
        return results;
    }
}