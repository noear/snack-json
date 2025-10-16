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


import org.noear.snack4.SnackException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类型工具类
 *
 * @author noear
 * @since 4.0
 * */
public class TypeUtil {
    private static Map<String, EnumWrap> enumCached = new ConcurrentHashMap<>();

    public static EnumWrap createEnum(Class<?> clz) {
        String key = clz.getName();
        EnumWrap val = enumCached.get(key);
        if (val == null) {
            val = new EnumWrap(clz);
            enumCached.put(key, val);
        }

        return val;
    }

    public static Type getCollectionItemType(Type fieldType) {
        if (fieldType instanceof ParameterizedType) {
            return getCollectionItemType((ParameterizedType) fieldType);
        }
        if (fieldType instanceof Class<?>) {
            return getCollectionItemType((Class<?>) fieldType);
        }
        return Object.class;
    }

    private static Type getCollectionItemType(Class<?> clazz) {
        return clazz.getName().startsWith("java.")
                ? Object.class
                : getCollectionItemType(getCollectionSuperType(clazz));
    }

    private static Type getCollectionSuperType(Class<?> clazz) {
        Type ct = null;
        for (Type type : clazz.getGenericInterfaces()) {
            Class<?> rawClass = getRawClass(type);
            if (rawClass == Collection.class) {
                return type;
            }
            if (Collection.class.isAssignableFrom(rawClass)) {
                ct = type;
            }
        }
        return ct == null ? clazz.getGenericSuperclass() : ct;
    }

    private static Type getCollectionItemType(ParameterizedType parameterizedType) {
        Type rawType = parameterizedType.getRawType();
        Type[] ata = parameterizedType.getActualTypeArguments();
        if (rawType == Collection.class) {
            return getWildcardTypeUpperBounds(ata[0]);
        }

        Class<?> rc = (Class<?>) rawType;
        Map<TypeVariable, Type> tp = createTypeParameterMap(rc.getTypeParameters(), ata);
        Type superType = getCollectionSuperType(rc);

        if (superType instanceof ParameterizedType) {
            Class<?> superClass = getRawClass(superType);
            Type[] at = ((ParameterizedType) superType).getActualTypeArguments();
            return at.length > 0
                    ? getCollectionItemType(makeParameterizedType(superClass, at, tp))
                    : getCollectionItemType(superClass);
        }

        return getCollectionItemType((Class<?>) superType);
    }

    private static Map<TypeVariable, Type> createTypeParameterMap(TypeVariable[] typeParameters, Type[] actualTypeArguments) {
        int length = typeParameters.length;
        Map<TypeVariable, Type> tp = new HashMap<TypeVariable, Type>(length);
        for (int i = 0; i < length; i++) {
            tp.put(typeParameters[i], actualTypeArguments[i]);
        }
        return tp;
    }

    private static ParameterizedType makeParameterizedType(Class<?> rawClass, Type[] typeParameters, Map<TypeVariable, Type> typeParameterMap) {
        int length = typeParameters.length;
        Type[] at = new Type[length];
        System.arraycopy(typeParameters, 0, at, 0, length);

        for (int i = 0; i < at.length; i++) {
            Type at1 = at[i];
            if (at1 instanceof TypeVariable) {
                at[i] = typeParameterMap.get(at1);
            }
        }

        return new ParameterizedTypeImpl(rawClass, at, null);
    }

    private static Type getWildcardTypeUpperBounds(Type type) {
        if (type instanceof WildcardType) {
            WildcardType wt = (WildcardType) type;
            Type[] ub = wt.getUpperBounds();
            return ub.length > 0 ? ub[0] : Object.class;
        }

        return type;
    }

    public static Class<?> getRawClass(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return getRawClass(((ParameterizedType) type).getRawType());
        } else {
            throw new SnackException("Unsupport type, class: " + type);
        }
    }
}