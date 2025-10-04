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

import org.noear.snack4.exception.ReflectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


public class ReflectionUtil {
    // 带缓存的字段获取
    private static final Map<Class<?>, Collection<FieldWrapper>> FIELD_CACHE = new ConcurrentHashMap<>();

    public static Collection<FieldWrapper> getDeclaredFields(Class<?> clazz) {
        return FIELD_CACHE.computeIfAbsent(clazz, k -> {
            Map<String, FieldWrapper> fields = new LinkedHashMap<>();
            Class<?> current = clazz;
            while (current != null) {
                for (Field field : current.getDeclaredFields()) {
                    field.setAccessible(true);
                    fields.put(field.getName(), new FieldWrapper(field));
                }
                current = current.getSuperclass();
            }
            return fields.values();
        });
    }

    // 安全创建实例
    public static <T> T newInstance(Class<T> clazz) {
        return newInstance(clazz, e -> new ReflectionException("Create instance failed: " + clazz.getName(), e));
    }

    public static <T> T newInstance(Class<T> clazz, Function<Exception, RuntimeException> exceptionHandler) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw exceptionHandler.apply(e);
        }
    }

    // 带错误处理的字段设值
    public static void setField(Object target, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("Set field failed: " + field.getName(), e);
        }
    }
}