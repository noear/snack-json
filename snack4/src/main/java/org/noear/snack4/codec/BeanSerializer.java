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
package org.noear.snack4.codec;

import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttrHolder;
import org.noear.snack4.codec.util.*;

import java.lang.reflect.Array;
import java.util.*;

/**
 * 对象编码器
 *
 * @author noear 2025/3/16 created
 * @since 4.0
 */
public class BeanSerializer {
    // 序列化：对象转ONode
    public static ONode serialize(Object value) {
        return serialize(value, null);
    }

    public static ONode serialize(Object value, Options opts) {
        if (value == null) {
            return new ONode(opts, null);
        }

        if (value instanceof ONode) {
            return (ONode) value;
        }

        if (opts == null) {
            opts = Options.DEF_OPTIONS;
        }

        try {
            ONode oNode = convertValueToNode(value, null, new IdentityHashMap<>(), opts);

            if (oNode.isObject() && opts.hasFeature(Feature.Write_NotRootClassName)) {
                oNode.remove(opts.getTypePropertyName());
            }

            return oNode;
        } catch (Throwable e) {
            if (e instanceof StackOverflowError) {
                throw (StackOverflowError) e;
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException("Failed to convert bean to ONode", e);
            }
        }
    }

    // 值转ONode处理
    private static ONode convertValueToNode(Object value, ONodeAttrHolder attr, Map<Object, Object> visited, Options opts) throws Exception {
        if (value == null) {
            return new ONode(opts, null);
        }

        if (value instanceof ONode) {
            return (ONode) value;
        }

        if (value instanceof ObjectEncoder) {
            return ((ObjectEncoder) value).encode(new EncodeContext(opts, attr), value, new ONode(opts));
        }

        // 优先使用自定义编解码器
        ObjectEncoder codec = opts.getEncoder(value);
        if (codec != null) {
            return codec.encode(new EncodeContext(opts, attr), value, new ONode(opts));
        }

        if (value instanceof Collection) {
            return convertCollectionToNode((Collection<?>) value, visited, opts);
        } else if (value instanceof Map) {
            return convertMapToNode((Map<?, ?>) value, visited, opts);
        } else {
            if (value.getClass().isArray()) {
                return convertArrayToNode(value, visited, opts);
            } else {
                return convertBeanToNode(value, visited, opts);
            }
        }
    }

    // 对象转ONode核心逻辑
    private static ONode convertBeanToNode(Object bean, Map<Object, Object> visited, Options opts) throws Exception {
        // 循环引用检测
        if (visited.containsKey(bean)) {
            return null;
            //throw new StackOverflowError("Circular reference detected: " + bean.getClass().getName());
        } else {
            visited.put(bean, null);
        }

        ONode tmp = new ONode(opts).asObject();

        try {
            if (isWriteClassName(opts, bean)) {
                tmp.set(opts.getTypePropertyName(), bean.getClass().getName());
            }

            boolean useOnlyGetter = opts.hasFeature(Feature.Read_OnlyUseGetter);
            boolean useGetter = useOnlyGetter || opts.hasFeature(Feature.Read_AllowUseGetter);

            ClassWrap classWrap = ClassWrap.from(TypeWrap.from(bean.getClass()));

            for (Map.Entry<String, PropertyWrap> entry : classWrap.getPropertyWraps().entrySet()) {
                PropertyWrap propertyWrap = entry.getValue();
                final Property property;

                if (useOnlyGetter) {
                    if (propertyWrap.getGetterWrap() != null) {
                        property = propertyWrap.getGetterWrap();
                    } else {
                        continue;
                    }
                } else {
                    if (useGetter && propertyWrap.getGetterWrap() != null) {
                        property = propertyWrap.getGetterWrap();
                    } else {
                        property = propertyWrap.getFieldWrap();
                    }
                }

                if (property == null || property.getAttr().isSerialize() == false) {
                    continue;
                }

                Object propertyValue = property.getValue(bean);

                if (propertyValue == null) {
                    if (opts.hasFeature(Feature.Write_Nulls) == false
                            && property.getAttr().hasSerializeFeature(Feature.Write_Nulls) == false) {
                        continue;
                    }

                    if (property.getTypeWrap().isString()) {
                        if ((opts.hasFeature(Feature.Write_NullStringAsEmpty) || property.getAttr().hasSerializeFeature(Feature.Write_NullStringAsEmpty))) {
                            propertyValue = "";
                        }
                    } else if (property.getTypeWrap().isBoolean()) {
                        if ((opts.hasFeature(Feature.Write_NullBooleanAsFalse) || property.getAttr().hasSerializeFeature(Feature.Write_NullBooleanAsFalse))) {
                            propertyValue = false;
                        }
                    } else if (property.getTypeWrap().isNumber()) {
                        if ((opts.hasFeature(Feature.Write_NullNumberAsZero) || property.getAttr().hasSerializeFeature(Feature.Write_NullNumberAsZero))) {
                            if (property.getTypeWrap().getType() == Long.class) {
                                propertyValue = 0L;
                            } else if (property.getTypeWrap().getType() == Double.class) {
                                propertyValue = 0D;
                            } else if (property.getTypeWrap().getType() == Float.class) {
                                propertyValue = 0F;
                            } else {
                                propertyValue = 0;
                            }
                        }
                    }
                }

                ONode propertyNode = convertValueToNode(propertyValue, property.getAttr(), visited, opts);

                if (propertyNode != null) {
                    if (property.getAttr().isFlat()) {
                        if (propertyNode.isObject()) {
                            tmp.setAll(propertyNode.getObject());
                        }
                    } else {
                        tmp.set(property.getName(), propertyNode);
                    }
                }
            }
        } finally {
            visited.remove(bean);
        }

        return tmp;
    }

    // 处理数组类型
    private static ONode convertArrayToNode(Object array, Map<Object, Object> visited, Options opts) throws Exception {
        ONode tmp = new ONode(opts).asArray();
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            tmp.add(convertValueToNode(Array.get(array, i), null, visited, opts));
        }
        return tmp;
    }

    // 处理集合类型
    private static ONode convertCollectionToNode(Collection<?> collection, Map<Object, Object> visited, Options opts) throws Exception {
        ONode tmp = new ONode(opts).asArray();
        for (Object item : collection) {
            tmp.add(convertValueToNode(item, null, visited, opts));
        }
        return tmp;
    }

    // 处理Map类型
    private static ONode convertMapToNode(Map<?, ?> map, Map<Object, Object> visited, Options opts) throws Exception {
        if (visited.containsKey(map)) {
            return null;
        } else {
            visited.put(map, null);
        }

        try {
            ONode tmp = new ONode(opts).asObject();

            if (isWriteClassName(opts, map)) {
                tmp.set(opts.getTypePropertyName(), map.getClass().getName());
            }

            for (Map.Entry<?, ?> entry : map.entrySet()) {
                ONode valueNode = convertValueToNode(entry.getValue(), null, visited, opts);
                tmp.set(String.valueOf(entry.getKey()), valueNode);
            }
            return tmp;
        } finally {
            visited.remove(map);
        }
    }

    private static boolean isWriteClassName(Options opts, Object obj) {
        if (opts.hasFeature(Feature.Write_ClassName) == false) {
            return false;
        }

        if (obj == null) {
            return false;
        }

        return true;
    }
}