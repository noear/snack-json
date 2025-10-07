package org.noear.snack4.codec.util;

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

    private final Type genericType;
    private final Map<String, Type> genericInfo;

    private Class<?> type = Object.class;
    private Constructor<?> constructor;

    public TypeWrap(Type genericType) {
        if (genericType instanceof Class<?>) {
            if (genericType instanceof Class) {
                Class<?> clazz = (Class<?>) genericType;
                if (clazz.isAnonymousClass()) {
                    genericType = clazz.getGenericSuperclass();
                }
            }
        }


        this.genericInfo = GenericUtil.getGenericInfo(genericType);
        this.genericType = GenericUtil.reviewType(genericType, this.genericInfo);

        if (genericType instanceof Class<?>) {
            type = (Class<?>) genericType;
        } else if (isParameterizedType()) {
            Type tmp = getParameterizedType().getRawType();

            if (tmp instanceof Class) {
                type = (Class<?>) tmp;
            }
        } else if (isGenericArrayType()) {
            type = Object[].class;
        } else if (isTypeVariable()) {
            Type tmp = getTypeVariable().getBounds()[0];

            if (tmp instanceof Class) {
                type = (Class<?>) tmp;
            }
        }

        if (type != Object.class) {
            for (Constructor c1 : type.getDeclaredConstructors()) {
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

    public Class<?> getType() {
        return type;
    }

    public Type getGenericType() {
        return genericType;
    }

    public Map<String, Type> getGenericInfo() {
        return genericInfo;
    }

    public boolean isInterface(){
        return type.isInterface();
    }

    public boolean isArray(){
        return type.isArray();
    }

    public boolean isEnum(){
        return type.isEnum();
    }

    public boolean isParameterizedType() {
        return genericType instanceof ParameterizedType;
    }

    public ParameterizedType getParameterizedType() {
        return (ParameterizedType) genericType;
    }

    public Type[] getActualTypeArguments() {
        return getParameterizedType().getActualTypeArguments();
    }

    public boolean isGenericArrayType() {
        return genericType instanceof GenericArrayType;
    }

    public GenericArrayType getGenericArrayType() {
        return (GenericArrayType) genericType;
    }

    public boolean isTypeVariable() {
        return genericType instanceof TypeVariable;
    }

    public TypeVariable getTypeVariable() {
        return (TypeVariable) genericType;
    }

    @Override
    public String toString() {
        return genericType.toString();
    }
}