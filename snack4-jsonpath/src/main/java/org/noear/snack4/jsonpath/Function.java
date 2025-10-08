package org.noear.snack4.jsonpath;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;

import java.util.List;
import java.util.function.BiFunction;

/**
 * JsonPath 函数
 *
 * @author noear 2025/10/8 created
 * @since 4.0
 */
@FunctionalInterface
public interface Function extends BiFunction<Options, List<ONode>, ONode> {
}
