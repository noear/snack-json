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

import org.noear.eggg.ClassEggg;
import org.noear.eggg.Property;
import org.noear.eggg.PropertyEggg;
import org.noear.eggg.TypeEggg;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttrHolder;
import org.noear.snack4.codec.util.*;
import org.noear.snack4.util.Asserts;

import java.lang.reflect.Array;
import java.util.*;

/**
 * 对象编码器
 *
 * @author noear 2025/3/16 created
 * @since 4.0
 */
public class BeanEncoder {
    /**
     * Java Object 编码为 ONode
     */
    public static ONode encode(Object value) {
        return encode(value, null);
    }

    /**
     * Java Object 编码为 ONode
     *
     * @param opts 选项
     */
    public static ONode encode(Object value, Options opts) {
        if (value == null) {
            return new ONode(opts, null);
        }

        if (value instanceof ONode) {
            return (ONode) value;
        }

        return new BeanEncoder(value, opts).encode();
    }

    private final Object source0;
    private final Options opts;

    private final Map<Object, Object> visited;

    private final boolean Write_Nulls;
    private final boolean onlyUseGetter;
    private final boolean allowUseGetter;

    private BeanEncoder(Object value, Options opts) {
        this.source0 = value;
        this.opts = opts == null ? Options.DEF_OPTIONS : opts;
        this.visited = new IdentityHashMap<>();

        this.Write_Nulls = this.opts.hasFeature(Feature.Write_Nulls);
        this.onlyUseGetter = this.opts.hasFeature(Feature.Read_OnlyUseGetter);
        this.allowUseGetter = onlyUseGetter || this.opts.hasFeature(Feature.Read_AllowUseGetter);
    }

    /**
     * Java Object 编码为 ONode
     */
    public ONode encode() {
        try {
            ONode oNode = encodeValueToNode(source0, null);

            if (oNode.isObject() && opts.hasFeature(Feature.Write_NotRootClassName)) {
                oNode.remove(opts.getTypePropertyName());
            }

            return oNode;
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new CodecException("Failed to encode bean to ONode", e);
        }
    }

    // 值转ONode处理
    private ONode encodeValueToNode(Object value, ONodeAttrHolder attr) throws Throwable {
        if (value == null) {
            if (Write_Nulls) {
                return new ONode(opts, null);
            } else {
                return null;
            }
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
            return encodeCollectionToNode((Collection<?>) value);
        } else if (value instanceof Map) {
            return encodeMapToNode((Map<?, ?>) value);
        } else {
            if (value.getClass().isArray()) {
                return encodeArrayToNode(value);
            } else {
                return encodeBeanToNode(value);
            }
        }
    }

    // 对象转ONode核心逻辑
    private ONode encodeBeanToNode(Object bean) throws Throwable {
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

            ClassEggg classEggg = EgggUtil.getTypeEggg(bean.getClass()).getClassEggg();

            for (PropertyEggg pw : classEggg.getPropertyEgggs()) {
                final Property property;
                if (onlyUseGetter) {
                    property = pw.getGetterEggg();
                } else if (allowUseGetter && pw.getGetterEggg() != null) {
                    property = pw.getGetterEggg();
                } else {
                    property = pw.getFieldEggg();
                }

                if (property == null) {
                    continue;
                }

                ONodeAttrHolder attr = property.getDigest();
                if (property.isTransient() || attr.isEncode() == false) {
                    continue;
                }

                ONode propertyNode = encodeBeanPropertyToNode(bean, property);

                if (propertyNode != null) {
                    if (attr.isFlat()) {
                        if (propertyNode.isObject()) {
                            tmp.setAll(propertyNode.getObject());
                        }
                    } else {
                        tmp.set(property.getAlias(), propertyNode);
                    }
                }
            }
        } finally {
            visited.remove(bean);
        }

        return tmp;
    }

    private ONode encodeBeanPropertyToNode(Object bean, Property property) throws Throwable {
        Object propValue = property.getValue(bean);
        ONode propNode = null;

        ONodeAttrHolder attr = property.getDigest();

        if (attr.getEncoder() != null) {
            propNode = attr.getEncoder().encode(new EncodeContext(opts, attr), propValue, new ONode(opts));
        } else {
            if (propValue == null) {
                TypeEggg ptw = property.getTypeEggg();
                //分类控制
                if (ptw.getType() == List.class) {
                    if ((opts.hasFeature(Feature.Write_NullListAsEmpty) || attr.hasFeature(Feature.Write_NullListAsEmpty))) {
                        propValue = new ArrayList<>();
                    }
                } else if (ptw.isString()) {
                    if ((opts.hasFeature(Feature.Write_NullStringAsEmpty) || attr.hasFeature(Feature.Write_NullStringAsEmpty))) {
                        propValue = "";
                    }
                } else if (ptw.isBoolean()) {
                    if ((opts.hasFeature(Feature.Write_NullBooleanAsFalse) || attr.hasFeature(Feature.Write_NullBooleanAsFalse))) {
                        propValue = false;
                    }
                } else if (ptw.isNumber()) {
                    if ((opts.hasFeature(Feature.Write_NullNumberAsZero) || attr.hasFeature(Feature.Write_NullNumberAsZero))) {
                        if (ptw.getType() == Long.class) {
                            propValue = 0L;
                        } else if (ptw.getType() == Double.class) {
                            propValue = 0D;
                        } else if (ptw.getType() == Float.class) {
                            propValue = 0F;
                        } else {
                            propValue = 0;
                        }
                    }
                }

                //托底控制
                if (propValue == null) {
                    if (Write_Nulls == false && attr.hasFeature(Feature.Write_Nulls) == false) {
                        return null;
                    }
                }
            }

            if (propValue instanceof Date) {
                if (Asserts.isNotEmpty(attr.getFormat())) {
                    String dateStr = attr.formatDate((Date) propValue);
                    propNode = new ONode(opts, dateStr);
                }
            }

            if (propNode == null) {
                propNode = encodeValueToNode(propValue, attr);
            }
        }

        return propNode;
    }

    // 处理数组类型
    private ONode encodeArrayToNode(Object array) throws Throwable {
        ONode tmp = new ONode(opts).asArray();
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            tmp.add(encodeValueToNode(Array.get(array, i), null));
        }
        return tmp;
    }

    // 处理集合类型
    private ONode encodeCollectionToNode(Collection<?> collection) throws Throwable {
        ONode tmp = new ONode(opts).asArray();
        for (Object item : collection) {
            tmp.add(encodeValueToNode(item, null));
        }
        return tmp;
    }

    // 处理Map类型
    private ONode encodeMapToNode(Map<?, ?> map) throws Throwable {
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
                ONode valueNode = encodeValueToNode(entry.getValue(), null);

                if (valueNode != null) {
                    tmp.set(String.valueOf(entry.getKey()), valueNode);
                }
            }
            return tmp;
        } finally {
            visited.remove(map);
        }
    }

    private boolean isWriteClassName(Options opts, Object obj) {
        if (obj == null) {
            return false;
        }

        if (opts.hasFeature(Feature.Write_ClassName) == false) {
            return false;
        }

        if (obj instanceof Map && opts.hasFeature(Feature.Write_NotMapClassName)) {
            return false;
        }

        return true;
    }
}