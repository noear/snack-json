package org.noear.snack4.codec;

import org.noear.snack4.Options;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public interface ObjectFactory<T> {
    T create(Options opts, Class<T> clazz);
}
