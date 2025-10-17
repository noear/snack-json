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

    private BeanEncoder(Object value, Options opts) {
        this.source0 = value;
        this.opts = opts == null ? Options.DEF_OPTIONS : opts;
        this.visited = new IdentityHashMap<>();

        Write_Nulls = opts.hasFeature(Feature.Write_Nulls);
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
    private ONode encodeValueToNode(Object value, ONodeAttrHolder attr) throws Exception {
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
    private ONode encodeBeanToNode(Object bean) throws Exception {
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

                if (property == null || property.getAttr().isEncode() == false) {
                    continue;
                }

                ONode propertyNode = encodeBeanPropertyToNode(bean, property);

                if (propertyNode != null) {
                    if (property.getAttr().isFlat()) {
                        if (propertyNode.isObject()) {
                            tmp.setAll(propertyNode.getObject());
                        }
                    } else {
                        tmp.set(property.getNodeName(), propertyNode);
                    }
                }
            }
        } finally {
            visited.remove(bean);
        }

        return tmp;
    }

    private ONode encodeBeanPropertyToNode(Object bean, Property property) throws Exception {
        Object propValue = property.getValue(bean);
        ONode propNode = null;

        if (property.getAttr().getEncoder() != null) {
            propNode = property.getAttr()
                    .getEncoder()
                    .encode(new EncodeContext(opts, property.getAttr()), propValue, new ONode(opts));
        } else {
            if (propValue == null) {
                //分类控制
                if (property.getTypeWrap().isList()) {
                    if ((opts.hasFeature(Feature.Write_NullListAsEmpty) || property.getAttr().hasFeature(Feature.Write_NullListAsEmpty))) {
                        propValue = new ArrayList<>();
                    }
                } else if (property.getTypeWrap().isString()) {
                    if ((opts.hasFeature(Feature.Write_NullStringAsEmpty) || property.getAttr().hasFeature(Feature.Write_NullStringAsEmpty))) {
                        propValue = "";
                    }
                } else if (property.getTypeWrap().isBoolean()) {
                    if ((opts.hasFeature(Feature.Write_NullBooleanAsFalse) || property.getAttr().hasFeature(Feature.Write_NullBooleanAsFalse))) {
                        propValue = false;
                    }
                } else if (property.getTypeWrap().isNumber()) {
                    if ((opts.hasFeature(Feature.Write_NullNumberAsZero) || property.getAttr().hasFeature(Feature.Write_NullNumberAsZero))) {
                        if (property.getTypeWrap().getType() == Long.class) {
                            propValue = 0L;
                        } else if (property.getTypeWrap().getType() == Double.class) {
                            propValue = 0D;
                        } else if (property.getTypeWrap().getType() == Float.class) {
                            propValue = 0F;
                        } else {
                            propValue = 0;
                        }
                    }
                }

                //托底控制
                if (propValue == null) {
                    if (Write_Nulls == false
                            && property.getAttr().hasFeature(Feature.Write_Nulls) == false) {
                        return null;
                    }
                }
            }

            if (propValue instanceof Date) {
                if (Asserts.isNotEmpty(property.getAttr().getFormat())) {
                    String dateStr = property.getAttr().formatDate((Date) propValue);
                    propNode = new ONode(opts, dateStr);
                }
            }

            if (propNode == null) {
                propNode = encodeValueToNode(propValue, property.getAttr());
            }
        }

        return propNode;
    }

    // 处理数组类型
    private ONode encodeArrayToNode(Object array) throws Exception {
        ONode tmp = new ONode(opts).asArray();
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            tmp.add(encodeValueToNode(Array.get(array, i), null));
        }
        return tmp;
    }

    // 处理集合类型
    private ONode encodeCollectionToNode(Collection<?> collection) throws Exception {
        ONode tmp = new ONode(opts).asArray();
        for (Object item : collection) {
            tmp.add(encodeValueToNode(item, null));
        }
        return tmp;
    }

    // 处理Map类型
    private ONode encodeMapToNode(Map<?, ?> map) throws Exception {
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