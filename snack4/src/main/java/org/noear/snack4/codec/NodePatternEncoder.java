package org.noear.snack4.codec;

/**
 * ONode 编码（用于自定义编码）
 *
 * @author noear
 * @since 3.2
 * */
public interface NodePatternEncoder<T> extends NodeEncoder<T> {
    /**
     * 可以编码的
     */
    boolean canEncode(Class clazz);
}
