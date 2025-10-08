package org.noear.snack4.codec;

import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttrHolder;
import org.noear.snack4.codec.util.TypeWrap;

import java.lang.reflect.Type;

/**
 *
 * @author noear 2025/10/7 created
 *
 */
public class DecodeContext<T> {
    private final Options options;
    private final ONodeAttrHolder attr;
    private final T target;
    private final Class<?> type;
    private final Type genericType;

    public DecodeContext(Options options, ONodeAttrHolder attr, T target, TypeWrap typeWrap) {
        this.options = options;
        this.attr = attr;
        this.target = target;
        this.type = typeWrap.getType();
        this.genericType = typeWrap.getGenericType();
    }

    public Options getOptions() {
        return options;
    }

    public ONodeAttrHolder getAttr() {
        return attr;
    }

    public T getTarget() {
        return target;
    }

    public Class<?> getType() {
        return type;
    }

    public Type getGenericType() {
        return genericType;
    }
}
