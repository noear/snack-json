package org.noear.snack4.codec;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;

/**
 * 对象解码器
 *
 * @author noear
 * @since 4.0
 */
public interface ObjectDecoder<T> {
    T decode(Options opts, ONodeAttr attr, ONode node, Class<?> clazz);
}