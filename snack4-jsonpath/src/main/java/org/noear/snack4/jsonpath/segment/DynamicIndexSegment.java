package org.noear.snack4.jsonpath.segment;

import org.noear.snack4.ONode;
import org.noear.snack4.exception.PathResolutionException;
import org.noear.snack4.json.JsonSource;
import org.noear.snack4.jsonpath.Context;
import org.noear.snack4.jsonpath.JsonPath;
import org.noear.snack4.jsonpath.QueryMode;
import org.noear.snack4.jsonpath.SegmentFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author noear 2025/10/8 created
 *
 */
public class DynamicIndexSegment implements SegmentFunction {
    private final String dynamicPath;

    public DynamicIndexSegment(String dynamicPath) {
        this.dynamicPath = dynamicPath;
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
            ONode dynamicResult = JsonPath.select(context.root, dynamicPath);

            if (dynamicResult.isNumber()) {
                forIndex(Arrays.asList(node), dynamicResult.getInt(), mode, results);
            } else if (dynamicResult.isString()) {
                forKey(Arrays.asList(node), dynamicResult.getString(), mode, results);
            }

        }
        return results;
    }

    private void forKey(List<ONode> currentNodes, String key, QueryMode mode, List<ONode> result) {
        currentNodes.stream()
                .filter(o -> {
                    if (mode == QueryMode.CREATE) {
                        o.asObject();
                        return true;
                    } else {
                        return o.isObject();
                    }
                })
                .map(obj -> {
                    if (mode == QueryMode.CREATE) {
                        obj.getOrNew(key);
                    }

                    ONode n1 = obj.getOrNull(key);
                    if (n1.source == null) {
                        n1.source = new JsonSource(obj, key, 0);
                    }

                    return n1;
                })
                .forEach(result::add);
    }

    private void forIndex(List<ONode> currentNodes, int index, QueryMode mode, List<ONode> result) {
        currentNodes.stream()
                .filter(o -> {
                    if (mode == QueryMode.CREATE) {
                        o.asArray();
                        return true;
                    } else {
                        return o.isArray();
                    }
                })
                .map(arr -> {
                    int idx = index;
                    if (idx < 0) {
                        idx = arr.size() + idx;
                    }

                    if (mode == QueryMode.CREATE) {
                        int count = idx + 1 - arr.size();
                        for (int i = 0; i < count; i++) {
                            arr.add(new ONode());
                        }
                    }

                    if (idx < 0 || idx >= arr.size()) {
                        throw new PathResolutionException("Index out of bounds: " + idx);
                    }

                    ONode n1 = arr.getOrNull(idx);
                    if (n1.source == null) {
                        n1.source = new JsonSource(arr, null, idx);
                    }

                    return n1;
                })
                .forEach(result::add);
    }
}