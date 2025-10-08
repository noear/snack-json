package org.noear.snack4.codec;

/**
 *
 * @author noear 2025/10/8 created
 *
 */
public interface ObjectPatternFactory<T> extends ObjectFactory<T> {
    boolean calCreate(Class<?> clazz);
}
