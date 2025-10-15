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

import org.noear.snack4.node.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.node.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.annotation.ONodeAttrHolder;
import org.noear.snack4.codec.util.*;
import org.noear.snack4.util.Asserts;

import java.lang.reflect.*;
import java.util.*;

/**
 * 对象解码器
 *
 * @author noear 2025/3/16 created
 * @since 4.0
 */
public class BeanDecoder {
    /**
     * ONode 解码为 Java Object
     *
     * @param type 类型
     *
     */
    public static <T> T decode(ONode node, Type type) {
        return decode(node, type, null, null);
    }

    /**
     * ONode 解码为 Java Object
     *
     * @param type 类型
     * @[param opts 选项
     *
     */
    public static <T> T decode(ONode node, Type type, Object target, Options opts) {
        if (node == null || type == null) {
            return null;
        }

        return new BeanDecoder(node, type, target, opts).decode();
    }

    private final ONode source0;
    private final Type targetType0;
    private final Object target0;

    private final Options opts;
    private final Map<Object, Object> visited;

    private BeanDecoder(ONode source, Type type, Object target, Options opts) {
        this.source0 = source;
        this.targetType0 = type;
        this.target0 = target;
        this.opts = opts == null ? Options.DEF_OPTIONS : opts;
        this.visited = new IdentityHashMap<>();
    }

    public <T> T decode() {
        TypeWrap typeWrap = TypeWrap.from(targetType0);


        try {
            return (T) decodeValueFromNode(source0, typeWrap, target0, null);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new CodecException("Failed to decode bean from ONode", e);
        }
    }

    // 类型转换核心
    private Object decodeValueFromNode(ONode node, TypeWrap typeWrap, Object target, ONodeAttrHolder attr) throws Exception {
        if (node.isNull()) {
            return null;
        }

        // 优先使用自定义编解码器
        //提前找到@type类型，便于自定义解码器定位
        typeWrap = confirmNodeType(node, typeWrap);

        // 优先使用自定义编解码器
        ObjectDecoder decoder = opts.getDecoder(typeWrap.getType());
        if (decoder != null) {
            return decoder.decode(new DecodeContext(opts, attr, target, typeWrap), node);
        }

        if (node.isValue()) {
            if (typeWrap.getType().isInterface() || Modifier.isAbstract(typeWrap.getType().getModifiers())) {
                if (node.isString() && node.getString().indexOf('.') > 0) {
                    Class<?> clz = opts.loadClass(node.getString());

                    if (clz == null) {
                        return null;
                    } else {
                        return ClassUtil.newInstance(clz);
                    }
                }
            }

            if (((Collection.class.isAssignableFrom(typeWrap.getType()) || typeWrap.getType().isArray()) && node.isString()) == false) {
                return node.getValue();
            }

        }

        if (target == null) {
            // 如果没有传入 target，则执行原有的创建新对象的逻辑
            ObjectCreator creator = opts.getCreator(typeWrap.getType());
            if (creator != null) {
                target = creator.create(opts, node, typeWrap.getType());
            }

            if (target == null) {
                if (typeWrap.getType().isInterface()) {
                    if (node.isEmpty()) {
                        return null;
                    }

                    throw new CodecException("can not convert bean to type: " + typeWrap.getType());
                }

                Constructor constructor = typeWrap.getConstructor();
                if (constructor == null) {
                    throw new CodecException("Create instance failed: " + typeWrap.getType().getName());
                }

                if (constructor.isAccessible() == false) {
                    constructor.setAccessible(true);
                }

                if (constructor.getParameterCount() == 0) {
                    target = constructor.newInstance();
                } else {
                    Object[] args = getConstructorArguments(typeWrap, node);
                    target = constructor.newInstance(args);
                }
            }
        }

        if (target instanceof Map) {
            target = decodeMapFromNode(node, typeWrap, target);
        } else if (target instanceof Collection) {
            target = decodeCollectionFromNode(node, typeWrap, target);
        } else {
            return decodeBeanFromNode(node, typeWrap, target);
        }

        return target;
    }

    private Object decodeBeanFromNode(ONode node, TypeWrap typeWrap, Object target) throws Exception {
        boolean useOnlySetter = opts.hasFeature(Feature.Write_OnlyUseSetter);
        boolean useSetter = useOnlySetter || opts.hasFeature(Feature.Write_AllowUseSetter);

        ClassWrap classWrap = ClassWrap.from(typeWrap);

        if (useOnlySetter) {
            //只能用 setter （以数据为主，支持 Read_FailOnUnknownProperties）
            for (Map.Entry<String, ONode> kv : node.getObject().entrySet()) {
                if (kv.getKey().startsWith(opts.getTypePropertyName())) {
                    continue;
                }

                if (typeWrap.getConstructor() != null) {
                    if (typeWrap.getParameterMap().containsKey(kv.getKey())) {
                        continue;
                    }
                }

                PropertyWrap propertyWrap = classWrap.getPropertyWrap(kv.getKey());

                if (propertyWrap != null) {
                    if (propertyWrap.getSetterWrap() != null) {
                        Property property = propertyWrap.getSetterWrap();
                        decodeBeanPropertyFromNode(node, property, target);
                    }
                } else if (opts.hasFeature(Feature.Write_FailOnUnknownProperties)) {
                    throw new CodecException("Unknown property : " + kv.getKey());
                }
            }
        } else {
            //允许用 setter （以类为主，支持 flat）
            for (Map.Entry<String, PropertyWrap> kv : classWrap.getPropertyWraps().entrySet()) {
                if (typeWrap.getConstructor() != null) {
                    if (typeWrap.getParameterMap().containsKey(kv.getKey())) {
                        continue;
                    }
                }

                PropertyWrap propertyWrap = kv.getValue();
                final Property property;

                if (useSetter && propertyWrap.getSetterWrap() != null) {
                    property = propertyWrap.getSetterWrap();
                } else {
                    property = propertyWrap.getFieldWrap();
                }

                if (property == null || property.getAttr().isDecode() == false) {
                    continue;
                }

                decodeBeanPropertyFromNode(node, property, target);
            }
        }

        return target;
    }

    private void decodeBeanPropertyFromNode(ONode node, Property property, Object target) throws Exception {
        ONode fieldNode = (property.getAttr().isFlat() ? node : node.get(property.getName()));

        if (fieldNode != null && !fieldNode.isNull()) {
            //深度填充：获取字段当前的值，作为递归调用的 target
            Object existingFieldValue = property.getValue(target);
            Object value = decodeValueFromNode(fieldNode, property.getTypeWrap(), existingFieldValue, property.getAttr());

            property.setValue(target, value);
        }
    }


    //-- 辅助方法 --//
    // 处理List泛型
    private Collection decodeCollectionFromNode(ONode node, TypeWrap typeWrap, Object target) throws Exception {
        Type elementType = Object.class;
        if (typeWrap.isParameterizedType()) {
            elementType = typeWrap.getActualTypeArguments()[0];
        }

        Collection coll = (Collection) target;

        if (node.isArray()) {
            if (coll == Collections.EMPTY_LIST) {
                coll = new ArrayList();
            } else if (coll == Collections.EMPTY_SET) {
                coll = new HashSet();
            }
            TypeWrap elementTypeWrap = TypeWrap.from(elementType);

            for (ONode n1 : node.getArray()) {
                //填充集合时，元素为新创建的，所以 target 传 null
                Object item = decodeValueFromNode(n1, elementTypeWrap, null, null);
                if (item != null) {
                    coll.add(item);
                }
            }
        } else if (node.isString()) {
            if (coll == Collections.EMPTY_LIST) {
                coll = new ArrayList();
            } else if (coll == Collections.EMPTY_SET) {
                coll = new HashSet();
            }

            // string 支持自动转数组
            String[] strArray = node.toString().split(",");
            TypeWrap elementTypeWrap = TypeWrap.from(elementType);

            for (String str : strArray) {
                Object item = decodeValueFromNode(new ONode(opts, str), elementTypeWrap, null, null);
                if (item != null) {
                    coll.add(item);
                }
            }
        } else {
            throw new CodecException("The type of node " + node.getType() + " cannot be converted to collection.");
        }

        return coll;
    }

    // 处理Map泛型
    private Map decodeMapFromNode(ONode node, TypeWrap targetTypeWrap, Object target) throws Exception {
        if (node.isObject()) {
            Type keyType = Object.class;
            Type valueType = Object.class;
            if (targetTypeWrap.isParameterizedType()) {
                keyType = targetTypeWrap.getActualTypeArguments()[0];
                valueType = targetTypeWrap.getActualTypeArguments()[1];
            }

            TypeWrap keyTypeWrap = TypeWrap.from(keyType);
            TypeWrap valueTypeWrap = TypeWrap.from(valueType);

            Map map = null;
            if (target != Collections.EMPTY_MAP) {
                map = (Map) target;
            } else {
                map = new LinkedHashMap<>();
            }

            for (Map.Entry<String, ONode> kv : node.getObject().entrySet()) {
                if (opts.hasFeature(Feature.Read_AutoType)) {
                    if (kv.getKey().startsWith(opts.getTypePropertyName())) {
                        continue;
                    }
                }

                //Map 的值是新对象，递归调用时 target 传 null
                Object k = decodeKey(kv.getKey(), keyTypeWrap);
                Object v = decodeValueFromNode(kv.getValue(), valueTypeWrap, null, null);
                map.put(k, v);
            }

            return map;
        } else {
            throw new CodecException("The type of node " + node.getType() + " cannot be converted to map.");
        }
    }

    // Map键类型转换
    private Object decodeKey(String key, TypeWrap keyType) {
        if (keyType.getType() == String.class || keyType.getType() == Object.class) return key;
        if (keyType.getType() == Integer.class || keyType.getType() == int.class) return Integer.parseInt(key);
        if (keyType.getType() == Long.class || keyType.getType() == long.class) return Long.parseLong(key);
        if (keyType.getType().isEnum()) {
            ObjectDecoder decoder = opts.getDecoder(keyType.getType());
            if (decoder == null) {
                return Enum.valueOf((Class<Enum>) keyType.getType(), key);
            } else {
                return decoder.decode(new DecodeContext(opts, null, null, keyType), new ONode(opts, key));
            }
        }

        throw new CodecException("Unsupported map key type: " + keyType.getType());
    }

    private Object[] getConstructorArguments(TypeWrap typeWrap, ONode node) throws Exception {
        //只有带参数的构造函（像 java record, kotlin data）
        Object[] argsV = new Object[typeWrap.getParameterAry().size()];

        for (int j = 0; j < argsV.length; j++) {
            Parameter p = typeWrap.getParameterAry().get(j);
            if (node.hasKey(p.getName())) {
                ONodeAttrHolder attr = new ONodeAttrHolder(p.getAnnotation(ONodeAttr.class), false);
                Object val = decodeValueFromNode(node.get(p.getName()), TypeWrap.from(p.getParameterizedType()), null, attr);
                argsV[j] = val;
            } else {
                argsV[j] = null;
            }
        }

        return argsV;
    }

    /**
     * 确认节点类型
     */
    private TypeWrap confirmNodeType(ONode oRef, TypeWrap def) {
        TypeWrap type0 = resolveNodeType(oRef, def);

        if (Throwable.class.isAssignableFrom(type0.getType())) {
            //如果有异常，则异常优先
            return type0;
        }

        if (def.getType() != Object.class
                && def.isInterface() == false
                && Modifier.isAbstract(def.getType().getModifiers()) == false) {
            // 如果自定义了类型，则自定义的类型优先
            return def;
        }

        return type0;
    }

    /**
     * 分析节点类型
     *
     */
    private TypeWrap resolveNodeType(ONode oRef, TypeWrap def) {
        if (oRef.isObject()) {
            String typeStr = null;
            if (isReadClassName(oRef)) {
                ONode n1 = oRef.getObject().get(opts.getTypePropertyName());
                if (n1 != null) {
                    typeStr = n1.getString();
                }
            }

            if (Asserts.isEmpty(typeStr) == false) {
                if (typeStr.startsWith("sun.") ||
                        typeStr.startsWith("com.sun.") ||
                        typeStr.startsWith("javax.") ||
                        typeStr.startsWith("jdk.")) {
                    throw new CodecException("Unsupported type, class: " + typeStr);
                }

                Class<?> clz = opts.loadClass(typeStr);
                if (clz == null) {
                    throw new CodecException("Unsupported type, class: " + typeStr);
                } else {
                    return TypeWrap.from(clz);
                }
            }
        }

        if (def.getType() == Object.class) {
            if (oRef.isObject()) {
                return TypeWrap.from(LinkedHashMap.class);
            }

            if (oRef.isArray()) {
                return TypeWrap.from(ArrayList.class);
            }
        }

        return def;
    }

    /**
     * 是否读取类名字
     */
    private boolean isReadClassName(ONode node) {
        if (opts.hasFeature(Feature.Read_AutoType) == false) {
            return false;
        }

        if (node.isObject() == false) {
            return false;
        }

        return true;
    }
}