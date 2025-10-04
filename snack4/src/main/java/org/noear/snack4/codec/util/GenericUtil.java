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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


public class GenericUtil {
    // 解析泛型类型
    public static Class<?> resolveRawType(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
        return Object.class;
    }

    // 获取泛型参数
    public static Type[] resolveTypeArguments(Type type) {
        return (type instanceof ParameterizedType) ?
                ((ParameterizedType) type).getActualTypeArguments() :
                new Type[0];
    }

    // 判断是否为集合类型
    public static boolean isCollectionType(Type type) {
        Class<?> rawType = resolveRawType(type);
        return Collection.class.isAssignableFrom(rawType);
    }

    // 判断是否为Map类型
    public static boolean isMapType(Type type) {
        Class<?> rawType = resolveRawType(type);
        return Map.class.isAssignableFrom(rawType);
    }

    // 创建泛型集合实例
    public static <T> Collection<T> createCollection(Type type) {
        Class<?> rawType = resolveRawType(type);
        if (List.class.isAssignableFrom(rawType)) return new ArrayList<>();
        if (Set.class.isAssignableFrom(rawType)) return new HashSet<>();
        throw new IllegalArgumentException("Unsupported collection type: " + type);
    }
}