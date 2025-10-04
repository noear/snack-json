package org.noear.snack4.codec;

import org.noear.snack4.Options;

/**
 * 对象工厂
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public interface ObjectFactory<T> {
    T create(Options opts, Class<T> clazz);
}