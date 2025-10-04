package org.noear.snack4.codec;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;

/**
 * 对象编码器
 *
 * @author noear
 * @since 4.0
 * */
public interface ObjectEncoder<T> {
    ONode encode(Options opts, ONodeAttr attr, T value);
}