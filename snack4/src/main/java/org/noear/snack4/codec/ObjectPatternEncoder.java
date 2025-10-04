package org.noear.snack4.codec;

/**
 * 对象模式编码器
 *
 * @author noear
 * @since 4.0
 * */
public interface ObjectPatternEncoder<T> extends ObjectEncoder<T> {
    /**
     * 可以编码的
     */
    boolean canEncode(Object value);
}