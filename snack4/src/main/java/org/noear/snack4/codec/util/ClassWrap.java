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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author noear 2025/10/7 created
 * @since 4.0
 */
public class ClassWrap {
    private static final Map<Type, ClassWrap> cached = new ConcurrentHashMap<>();

    public static ClassWrap from(TypeWrap typeWrap) {
        return cached.computeIfAbsent(typeWrap.getGenericType(), t -> new ClassWrap(typeWrap));
    }

    private final TypeWrap typeWrap;
    private final Map<String, FieldWrap> fieldWraps = new LinkedHashMap<>();
    private final Map<String, PropertyWrap> propertyWraps = new LinkedHashMap<>();
    private final Map<String, PropertyWrap> propertyNodeWraps = new LinkedHashMap<>();

    private ClassWrap(TypeWrap typeWrap) {
        this.typeWrap = typeWrap;
        loadDeclaredFields();
        loadDeclaredPropertys();

        for (Map.Entry<String, PropertyWrap> entry : propertyWraps.entrySet()) {
            propertyNodeWraps.put(entry.getValue().getNodeName(), entry.getValue());
        }
    }

    public TypeWrap getTypeWrap() {
        return typeWrap;
    }

    public Map<String, FieldWrap> getFieldWraps() {
        return fieldWraps;
    }

    public Map<String, PropertyWrap> getPropertyWraps() {
        return propertyWraps;
    }

    /**
     * 使用节点名获取属性
     *
     */
    public PropertyWrap getPropertyWrap(String nodeName) {
        return propertyNodeWraps.get(nodeName);
    }

    private void loadDeclaredFields() {
        Class<?> current = typeWrap.getType();

        while (current != null) {
            for (Field f : current.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) {
                    continue;
                }

                FieldWrap fieldWrap = new FieldWrap(typeWrap, f);

                fieldWraps.put(fieldWrap.getOrigName(), fieldWrap);
                propertyWraps.computeIfAbsent(fieldWrap.getOrigName(), k -> new PropertyWrap(k))
                        .setFieldWrap(fieldWrap);
            }
            current = current.getSuperclass();
        }
    }

    private void loadDeclaredPropertys() {
        for (Method m : typeWrap.getType().getMethods()) {
            if (m.getDeclaringClass() == Object.class) {
                continue;
            }

            if (m.getName().length() > 3) {
                if (m.getReturnType() == void.class && m.getParameterCount() == 1) {
                    //setter
                    if (m.getName().startsWith("set")) {
                        PropertyMethodWrap setterWrap = new PropertyMethodWrap(typeWrap, m);
                        FieldWrap fieldWrap = fieldWraps.get(setterWrap.getOrigName());
                        setterWrap.initAttr(fieldWrap);

                        propertyWraps.computeIfAbsent(setterWrap.getOrigName(), k -> new PropertyWrap(k))
                                .setSetterWrap(setterWrap);
                    }
                } else if (m.getReturnType() != void.class && m.getParameterCount() == 0) {
                    //getter
                    if (m.getName().startsWith("get")) {
                        PropertyMethodWrap getterWrap = new PropertyMethodWrap(typeWrap, m);
                        FieldWrap fieldWrap = fieldWraps.get(getterWrap.getOrigName());
                        getterWrap.initAttr(fieldWrap);

                        propertyWraps.computeIfAbsent(getterWrap.getOrigName(), k -> new PropertyWrap(k))
                                .setGetterWrap(getterWrap);
                    }
                }
            }
        }
    }
}
