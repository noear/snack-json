package org.noear.snack4.jsonpath.selector;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.QueryContext;

import java.util.List;

/**
 * JsonPath 选择器
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public interface Selector {
    /**
     * 是否为多出
     */
    boolean isMultiple();

    /**
     * 是否为展开
     */
    boolean isExpanded();

    /**
     * 选择
     *
     * @param currentNodes 当前节点
     * @param ctx          查询上下文
     * @param results      结果
     */
    void select(QueryContext ctx, boolean isDescendant, List<ONode> currentNodes, List<ONode> results);
}