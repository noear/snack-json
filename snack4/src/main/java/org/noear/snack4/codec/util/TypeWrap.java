/*
 * Copyright 2005-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.snack4.codec.util;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author noear 2025/10/6 created
 * @since 4.0
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
    private Map<String,Parameter> parameterMap;
    private List<Parameter> parameterAry;

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

            if (constructor != null) {
                parameterMap = new HashMap<>();
                parameterAry = new ArrayList<>();

                for (Parameter p1 : constructor.getParameters()) {
                    parameterMap.put(p1.getName(), p1);
                    parameterAry.add(p1);
                }
            }
        }
    }

    public Constructor<?> getConstructor() {
       return constructor;
    }

    public Map<String, Parameter> getParameterMap() {
        return parameterMap;
    }

    public List<Parameter> getParameterAry() {
        return parameterAry;
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

    public boolean isList(){
        return type == List.class;
    }

    public boolean isString(){
        return String.class.isAssignableFrom(type);
    }

    public boolean isBoolean(){
        return Boolean.class.isAssignableFrom(type);
    }

    public boolean isNumber(){
        return Number.class.isAssignableFrom(type);
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