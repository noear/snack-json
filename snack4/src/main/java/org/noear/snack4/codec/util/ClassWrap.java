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

    private boolean likeRecordClass = true;

    private ClassWrap(TypeWrap typeWrap) {
        this.typeWrap = typeWrap;
        loadDeclaredFields();
        loadDeclaredPropertys();

        this.likeRecordClass = likeRecordClass && fieldWraps.size() > 0;

        for (Map.Entry<String, PropertyWrap> entry : propertyWraps.entrySet()) {
            propertyNodeWraps.put(entry.getValue().getNodeName(), entry.getValue());
        }
    }

    public boolean isLikeRecordClass() {
        return likeRecordClass;
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
        Class<?> c = typeWrap.getType();

        while (c != null) {
            for (Field f : c.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) {
                    continue;
                }

                FieldWrap fieldWrap = new FieldWrap(typeWrap, f);

                //如果全是只读，则
                likeRecordClass = likeRecordClass && fieldWrap.isFinal();

                fieldWraps.put(fieldWrap.getOrigName(), fieldWrap);
                propertyWraps.computeIfAbsent(fieldWrap.getOrigName(), k -> new PropertyWrap(k))
                        .setFieldWrap(fieldWrap);
            }
            c = c.getSuperclass();
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
                        PropertyMethodWrap sw = new PropertyMethodWrap(typeWrap, m);
                        FieldWrap fieldWrap = fieldWraps.get(sw.getOrigName());
                        sw.initAttr(fieldWrap);

                        propertyWraps.computeIfAbsent(sw.getOrigName(), k -> new PropertyWrap(k))
                                .setSetterWrap(sw);
                    }
                } else if (m.getReturnType() != void.class && m.getParameterCount() == 0) {
                    //getter
                    if (m.getName().startsWith("get")) {
                        PropertyMethodWrap gw = new PropertyMethodWrap(typeWrap, m);
                        FieldWrap fieldWrap = fieldWraps.get(gw.getOrigName());
                        gw.initAttr(fieldWrap);

                        propertyWraps.computeIfAbsent(gw.getOrigName(), k -> new PropertyWrap(k))
                                .setGetterWrap(gw);
                    }
                }
            }
        }
    }
}
