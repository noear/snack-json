package org.noear.snack4.codec;


import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;

/**
 * ONode 解码（用于控制自定义解码）
 *
 * @author noear
 * @since 3.2
 */
public interface NodeDecoder<T> {
    T decode(Options opts, ONodeAttr attr, ONode node, Class<?> clazz);
}
