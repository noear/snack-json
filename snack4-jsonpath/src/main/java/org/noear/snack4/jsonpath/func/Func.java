package org.noear.snack4.jsonpath.func;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.QueryContext;

import java.util.List;

/**
 * JsonPath 函数
 *
 * @author noear 2025/10/8 created
 * @since 4.0
 */
@FunctionalInterface
public interface Func {
    ONode apply(QueryContext ctx, List<ONode> oNodes);
}
