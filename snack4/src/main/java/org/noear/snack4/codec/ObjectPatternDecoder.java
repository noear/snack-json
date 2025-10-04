package org.noear.snack4.codec;

/**
 * 对象模式解码器
 *
 * @author noear 2025/10/4 created
 *
 */
public interface ObjectPatternDecoder<T> extends ObjectDecoder<T> {
    /**
     * 可以解码的
     */
    boolean canDecode(Class<?> clazz);
}
