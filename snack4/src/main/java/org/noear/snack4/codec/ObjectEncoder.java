package org.noear.snack4.codec;

import org.noear.snack4.ONode;

/**
 * 对象编码器
 *
 * @author noear
 * @since 4.0
 * */
public interface ObjectEncoder<T> {
    ONode encode(EncodeContext ctx, T value);
}