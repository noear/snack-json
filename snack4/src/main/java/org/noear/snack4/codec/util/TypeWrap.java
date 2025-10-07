package org.noear.snack4.codec.util;

import org.noear.snack4.util.Asserts;

import java.lang.reflect.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author noear 2025/10/6 created
 */
public class TypeWrap {
    private static Map<Type, TypeWrap> cached = new ConcurrentHashMap<>();

    public static TypeWrap from(Type type) {
        return cached.computeIfAbsent(type, t -> new TypeWrap(t));
    }


    private Type type;
    private Class<?> clazz = Object.class;
    private Constructor<?> constructor;

    public TypeWrap(Type type) {
        if (type instanceof Class<?>) {
            if (type instanceof Class) {
                Class<?> clazz = (Class<?>) type;
                if (clazz.isAnonymousClass()) {
                    type = clazz.getGenericSuperclass();
                }
            }
        }

        this.type = type;

        if (type instanceof Class<?>) {
            clazz = (Class<?>) type;
        } else if (isParameterizedType()) {
            Type tmp = getParameterizedType().getRawType();

            if (tmp instanceof Class) {
                clazz = (Class<?>) tmp;
            }
        } else if (isGenericArrayType()) {
            Type tmp = getGenericArrayType().getGenericComponentType();

            if (tmp instanceof Class) {
                clazz = (Class<?>) tmp;
            }
        } else if (isTypeVariable()) {
            Type tmp = getTypeVariable().getBounds()[0];

            if (tmp instanceof Class) {
                clazz = (Class<?>) tmp;
            }
        }

        if (clazz != Object.class) {
            for (Constructor c1 : clazz.getDeclaredConstructors()) {
                if (constructor == null) {
                    constructor = c1;
                } else if (constructor.getParameterCount() > c1.getParameterCount()) {
                    constructor = c1;
                }
            }
        }
    }

    public Constructor<?> getConstructor() {
       return constructor;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Type getType() {
        return type;
    }

    public boolean isInterface(){
        return clazz.isInterface();
    }

    public boolean isArray(){
        return clazz.isArray();
    }

    public boolean isEnum(){
        return clazz.isEnum();
    }

    public boolean isParameterizedType() {
        return type instanceof ParameterizedType;
    }

    public ParameterizedType getParameterizedType() {
        return (ParameterizedType) type;
    }

    public Type[] getActualTypeArguments() {
        return getParameterizedType().getActualTypeArguments();
    }

    public boolean isGenericArrayType() {
        return type instanceof GenericArrayType;
    }

    public GenericArrayType getGenericArrayType() {
        return (GenericArrayType) type;
    }

    public boolean isTypeVariable() {
        return type instanceof TypeVariable;
    }

    public TypeVariable getTypeVariable() {
        return (TypeVariable) type;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}