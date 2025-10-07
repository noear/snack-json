package org.noear.snack4.codec;

import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.util.TypeWrap;

import java.lang.reflect.Type;

/**
 *
 * @author noear 2025/10/7 created
 *
 */
public class DecodeContext<T> {
    private final Options opts;
    private final ONodeAttr attr;
    private final T target;
    private final Class<?> type;
    private final Type genericType;

    public DecodeContext(Options opts, ONodeAttr attr, T target, TypeWrap typeWrap) {
        this.opts = opts;
        this.attr = attr;
        this.target = target;
        this.type = typeWrap.getType();
        this.genericType = typeWrap.getGenericType();
    }

    public Options getOpts() {
        return opts;
    }

    public ONodeAttr getAttr() {
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
