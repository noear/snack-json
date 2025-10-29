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

import org.noear.eggg.*;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
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
    private final boolean useOnlySetter;
    private final boolean useSetter;

    private BeanDecoder(ONode source, Type type, Object target, Options opts) {
        this.source0 = source;
        this.targetType0 = type;
        this.target0 = target;
        this.opts = opts == null ? Options.DEF_OPTIONS : opts;
        this.visited = new IdentityHashMap<>();

        this.useOnlySetter = this.opts.hasFeature(Feature.Write_OnlyUseSetter);
        this.useSetter = useOnlySetter || this.opts.hasFeature(Feature.Write_AllowUseSetter);
    }

    public <T> T decode() {
        TypeEggg typeEggg = EgggUtil.getTypeEggg(targetType0);


        try {
            return (T) decodeValueFromNode(source0, typeEggg, target0, null);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new CodecException("Failed to decode bean from ONode", e);
        }
    }

    // 类型转换核心
    private Object decodeValueFromNode(ONode node, TypeEggg typeEggg, Object target, ONodeAttrHolder attr) throws Throwable {
        if (node.isNull()) {
            return null;
        }

        // 优先使用自定义编解码器
        //提前找到@type类型，便于自定义解码器定位
        typeEggg = confirmNodeType(node, typeEggg);

        // 优先使用自定义编解码器
        ObjectDecoder decoder = opts.getDecoder(typeEggg.getType());
        if (decoder != null) {
            return decoder.decode(new DecodeContext(opts, attr, target, typeEggg), node);
        }

        if (node.isValue()) {
            if (typeEggg.getType().isInterface() || Modifier.isAbstract(typeEggg.getType().getModifiers())) {
                if (node.isString() && node.getString().indexOf('.') > 0) {
                    Class<?> clz = opts.loadClass(node.getString());

                    if (clz == null) {
                        return null;
                    } else {
                        return ClassUtil.newInstance(clz);
                    }
                }
            }

            if (((Collection.class.isAssignableFrom(typeEggg.getType()) || typeEggg.getType().isArray()) && node.isString()) == false) {
                return node.getValue();
            }

        }

        if (target == null) {
            // 如果没有传入 target，则执行原有的创建新对象的逻辑
            ObjectCreator creator = opts.getCreator(typeEggg.getType());
            if (creator != null) {
                target = creator.create(opts, node, typeEggg.getType());
            }

            if (target == null) {
                if (typeEggg.getType().isInterface()) {
                    if (node.isEmpty()) {
                        return null;
                    }

                    throw new CodecException("can not convert bean to type: " + typeEggg.getType());
                }

                ConstrEggg constrEggg = typeEggg.getClassEggg().getCreator();
                if (constrEggg == null) {
                    throw new CodecException("Create instance failed: " + typeEggg.getType().getName());
                }

                if (constrEggg.getParamCount() == 0) {
                    target = constrEggg.newInstance();
                } else {
                    if (constrEggg.isSecurity() == false //有参数
                            && opts.hasFeature(Feature.Write_AllowParameterizedConstructor) == false //不支持参数
                            && typeEggg.getClassEggg().isLikeRecordClass() == false)  //不像记录类
                    {
                        throw new CodecException("Parameterized constructor are not allowed: " + typeEggg.getType());
                    }

                    Object[] args = getConstrArgs(constrEggg, node);
                    target = constrEggg.newInstance(args);
                }
            }
        }

        if (target instanceof Map) {
            target = decodeMapFromNode(node, typeEggg, target);
        } else if (target instanceof Collection) {
            target = decodeCollectionFromNode(node, typeEggg, target);
        } else {
            return decodeBeanFromNode(node, typeEggg, target);
        }

        return target;
    }

    private Object decodeBeanFromNode(ONode node, TypeEggg typeEggg, Object target) throws Throwable {
        boolean failOnUnknownProperties = opts.hasFeature(Feature.Write_FailOnUnknownProperties);

        ClassEggg classEggg = typeEggg.getClassEggg();

        if (failOnUnknownProperties) {
            //以数据为主，才能支持 Read_FailOnUnknownProperties
            for (Map.Entry<String, ONode> kv : node.getObject().entrySet()) {
                if (kv.getKey().startsWith(opts.getTypePropertyName())) {
                    continue;
                }

                if (classEggg.getCreator() != null) {
                    if (classEggg.getCreator().hasParamEgggByAlias(kv.getKey())) {
                        continue;
                    }
                }

                PropertyEggg pe = classEggg.getPropertyEgggByAlias(kv.getKey());

                if (pe != null) {
                    decodeBeanPropertyFromNode(node, pe, target);
                } else {
                    throw new CodecException("Unknown property : " + kv.getKey());
                }
            }
        } else {
            //以类为主，才能支持 flat
            for (PropertyEggg pe : classEggg.getPropertyEgggs()) {
                if (classEggg.getCreator() != null) {
                    if (classEggg.getCreator().hasParamEgggByAlias(pe.getAlias())) {
                        continue;
                    }
                }

                decodeBeanPropertyFromNode(node, pe, target);
            }
        }

        return target;
    }

    private void decodeBeanPropertyFromNode(ONode node, PropertyEggg pe, Object target) throws Throwable {
        final Property property;
        if (useOnlySetter) {
            property = pe.getSetterEggg();
        } else if (useSetter && pe.getSetterEggg() != null) {
            property = pe.getSetterEggg();
        } else {
            property = pe.getFieldEggg();
        }

        decodeBeanPropertyFromNode0(node, property, target);
    }

    private void decodeBeanPropertyFromNode0(ONode node, Property property, Object target) throws Throwable {
        if (property == null || property.isTransient() || property.<ONodeAttrHolder>getDigest().isDecode() == false) {
            return;
        }


        ONode oNode = (property.<ONodeAttrHolder>getDigest().isFlat() ? node : node.get(property.getAlias()));

        if (oNode != null && !oNode.isNull()) {
            //深度填充：获取字段当前的值，作为递归调用的 target
            Object exisValue = property.getValue(target);
            Object propValue = null;

            if (property.<ONodeAttrHolder>getDigest().getDecoder() != null) {
                propValue = property.<ONodeAttrHolder>getDigest()
                        .getDecoder()
                        .decode(new DecodeContext(opts, property.getDigest(), exisValue, property.getTypeEggg()), oNode);
            } else {
                propValue = decodeValueFromNode(oNode, property.getTypeEggg(), exisValue, property.getDigest());
            }

            property.setValue(target, propValue);
        }
    }


    //-- 辅助方法 --//
    // 处理List泛型
    private Collection decodeCollectionFromNode(ONode node, TypeEggg typeEggg, Object target) throws Throwable {
        Type elementType = Object.class;
        if (typeEggg.isParameterizedType()) {
            elementType = typeEggg.getActualTypeArguments()[0];
        }

        Collection coll = (Collection) target;

        if (node.isArray()) {
            if (coll == Collections.EMPTY_LIST) {
                coll = new ArrayList();
            } else if (coll == Collections.EMPTY_SET) {
                coll = new HashSet();
            }
            TypeEggg elementTypeEggg = EgggUtil.getTypeEggg(elementType);

            for (ONode n1 : node.getArray()) {
                //填充集合时，元素为新创建的，所以 target 传 null
                Object item = decodeValueFromNode(n1, elementTypeEggg, null, null);
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
            TypeEggg elementTypeEggg = EgggUtil.getTypeEggg(elementType);

            for (String str : strArray) {
                Object item = decodeValueFromNode(new ONode(opts, str), elementTypeEggg, null, null);
                if (item != null) {
                    coll.add(item);
                }
            }
        } else {
            throw new CodecException("The type of node " + node.type() + " cannot be converted to collection.");
        }

        return coll;
    }

    // 处理Map泛型
    private Map decodeMapFromNode(ONode node, TypeEggg targetTypeEggg, Object target) throws Throwable {
        if (node.isObject()) {
            Type keyType = Object.class;
            Type valueType = Object.class;
            if (targetTypeEggg.isParameterizedType()) {
                keyType = targetTypeEggg.getActualTypeArguments()[0];
                valueType = targetTypeEggg.getActualTypeArguments()[1];
            }

            TypeEggg keyTypeEggg = EgggUtil.getTypeEggg(keyType);
            TypeEggg valueTypeEggg = EgggUtil.getTypeEggg(valueType);

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
                Object k = decodeKey(kv.getKey(), keyTypeEggg);
                Object v = decodeValueFromNode(kv.getValue(), valueTypeEggg, null, null);
                map.put(k, v);
            }

            return map;
        } else {
            throw new CodecException("The type of node " + node.type() + " cannot be converted to map.");
        }
    }

    // Map键类型转换
    private Object decodeKey(String key, TypeEggg keyType) {
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

    private Object[] getConstrArgs(ConstrEggg constrEggg, ONode node) throws Throwable {
        //只有带参数的构造函（像 java record, kotlin data）
        Object[] argsV = new Object[constrEggg.getParamCount()];

        for (int j = 0; j < argsV.length; j++) {
            ParamEggg p = constrEggg.getParamEgggAt(j);
            if (node.hasKey(p.getAlias())) {
                ONodeAttrHolder attr = p.getDigest();
                Object val = decodeValueFromNode(node.get(p.getAlias()), p.getTypeEggg(), null, attr);
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
    private TypeEggg confirmNodeType(ONode oRef, TypeEggg def) {
        TypeEggg type0 = resolveNodeType(oRef, def);

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
    private TypeEggg resolveNodeType(ONode oRef, TypeEggg def) {
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
                    return EgggUtil.getTypeEggg(clz);
                }
            }
        }

        if (def.getType() == Object.class) {
            if (oRef.isObject()) {
                return EgggUtil.getTypeEggg(LinkedHashMap.class);
            }

            if (oRef.isArray()) {
                return EgggUtil.getTypeEggg(ArrayList.class);
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