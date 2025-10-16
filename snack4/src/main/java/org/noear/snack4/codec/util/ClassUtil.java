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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 *
 * @author noear 2019/2/12 created
 * @since 4.0
 */
public class ClassUtil {
    /**
     * 新建实例
     */
    public static <T> T newInstance(Class<T> clz) throws SnackException {
        try {
            if (clz.isInterface()) {
                return null;
            } else {
                Constructor<T> c = clz.getDeclaredConstructor();
                if (c.isAccessible() == false) {
                    c.setAccessible(true);
                }

                return c.newInstance();
            }
        } catch (Throwable e) {
            throw new SnackException("Instantiation failed: " + clz.getName(), e);
        }
    }

    private static Class<?> recordClass;

    /**
     * 是否为 Record 类
     */
    public static boolean isRecordClass(Class<?> clazz) {
        if (JavaUtil.JAVA_MAJOR_VERSION < 17) {
            return false;
        }

        if (clazz == null) {
            return false;
        }

        // 1. Record 类是 final 的
        if (!Modifier.isFinal(clazz.getModifiers())) {
            return false;
        }

        try {
            // 2. 通过 isAssignableFrom 检测
            if (recordClass == null) {
                recordClass = Class.forName("java.lang.Record");
            }

            return recordClass.isAssignableFrom(clazz);

        } catch (ClassNotFoundException e) {
            return false;
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }
}