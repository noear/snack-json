package org.noear.snack4.codec;

/**
 *
 * @author noear 2025/10/4 created
 *
 */
public interface NodePatternDecoder<T> extends NodeDecoder<T> {
    /**
     * 可以解码的
     */
    boolean canDecode(Class<?> clazz);
}
