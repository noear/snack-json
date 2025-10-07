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
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.util.ClassWrap;
import org.noear.snack4.codec.util.FieldWrap;
import org.noear.snack4.codec.util.ClassUtil;
import org.noear.snack4.codec.util.TypeWrap;
import org.noear.snack4.exception.ReflectionException;
import org.noear.snack4.exception.SnackException;
import org.noear.snack4.util.Asserts;

import java.lang.reflect.*;
import java.util.*;

/**
 * 对象解码器
 *
 * @author noear
 * @since 4.0
 */
public class BeanDeserializer {
    public static <T> T deserialize(ONode node, Type type) {
        return deserialize(node, type, null, null);
    }

    public static <T> T deserialize(ONode node, Type type, Object target, Options opts) {
        if (node == null || type == null) {
            return null;
        }

        if (opts == null) {
            opts = Options.def();
        }

        TypeWrap typeWrap = TypeWrap.from(type);

        try {
            return (T) convertValue(node, typeWrap, target, null, new IdentityHashMap<>(), opts);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    // 类型转换核心
    private static Object convertValue(ONode node, TypeWrap typeWrap, Object target, ONodeAttr attr, Map<Object, Object> visited, Options opts) throws Exception {
        if (node.isNull()) {
            return null;
        }

        // 优先使用自定义编解码器
        //提前找到@type类型，便于自定义解码器定位
        typeWrap = confirmNodeType(opts, node, typeWrap);

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
            ObjectFactory factory = opts.getFactory(typeWrap.getType());
            if (factory != null) {
                target = factory.create(opts, typeWrap.getType());
            }

            if (target == null) {
                if (typeWrap.getType().isInterface()) {
                    if (node.isNullOrEmpty()) {
                        return null;
                    }

                    throw new IllegalArgumentException("can not convert bean to type: " + typeWrap.getType());
                }

                Constructor constructor = typeWrap.getConstructor();
                if (constructor == null) {
                    throw new ReflectionException("Create instance failed: " + typeWrap.getType().getName());
                }

                if (constructor.getParameterCount() == 0) {
                    target = constructor.newInstance();
                } else {
                    Object[] args = getConstructorArguments(constructor, node, visited, opts);
                    target = constructor.newInstance(args);
                }
            }
        }

        if (target instanceof Map) {
            target = convertToMap(node, typeWrap, target, visited, opts);
        } else if (target instanceof Collection) {
            target = convertToCollection(node, typeWrap, target, visited, opts);
        } else {
            return convertToObject(node, typeWrap, target, visited, opts);
        }

        return target;
    }

    private static Object convertToObject(ONode node, TypeWrap typeWrap, Object target, Map<Object, Object> visited, Options opts) throws Exception {
        boolean useOnlySetter = opts.hasFeature(Feature.Write_UseOnlySetter);
        boolean useSetter = opts.hasFeature(Feature.Write_UseSetter);

        for (FieldWrap field : ClassWrap.from(typeWrap).getFieldWraps()) {
            if (useOnlySetter && field.hasSetter() == false) {
                continue;
            }

            if (field.isDeserialize()) {
                ONode fieldNode = node.get(field.getName());

                if (fieldNode != null && !fieldNode.isNull()) {
                    //深度填充：获取字段当前的值，作为递归调用的 target
                    Object existingFieldValue = field.getValue(target, false);
                    Object value = convertValue(fieldNode, field.getTypeWrap(), existingFieldValue, field.getAttr(), visited, opts);

                    if (field.isFinal() == false) {
                        field.setValue(target, value, useOnlySetter || useSetter);
                    }
                } else {
                    setPrimitiveDefault(field.getField(), target);
                }
            }
        }

        return target;
    }


    //-- 辅助方法 --//
    // 处理List泛型
    private static Collection convertToCollection(ONode node, TypeWrap typeWrap, Object target, Map<Object, Object> visited, Options opts) throws Exception {
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
                Object item = convertValue(n1, elementTypeWrap, null, null, visited, opts);
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
                Object item = convertValue(new ONode(str), elementTypeWrap, null, null, visited, opts);
                if (item != null) {
                    coll.add(item);
                }
            }
        } else {
            throw new IllegalArgumentException("The type of node " + node.getType() + " cannot be converted to collection.");
        }

        return coll;
    }

    // 处理Map泛型
    private static Map convertToMap(ONode node, TypeWrap targetTypeWrap, Object target, Map<Object, Object> visited, Options opts) throws Exception {
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
                //Map 的值是新对象，递归调用时 target 传 null
                Object k = convertKey(kv.getKey(), keyTypeWrap, opts);
                Object v = convertValue(kv.getValue(), valueTypeWrap, null, null, visited, opts);
                map.put(k, v);
            }

            return map;
        } else {
            throw new IllegalArgumentException("The type of node " + node.getType() + " cannot be converted to map.");
        }
    }

    // Map键类型转换
    private static Object convertKey(String key, TypeWrap keyType, Options opts) {
        if (keyType.getType() == String.class || keyType.getType() == Object.class) return key;
        if (keyType.getType() == Integer.class || keyType.getType() == int.class) return Integer.parseInt(key);
        if (keyType.getType() == Long.class || keyType.getType() == long.class) return Long.parseLong(key);
        if (keyType.getType().isEnum()) {
            ObjectDecoder decoder = opts.getDecoder(keyType.getType());
            if (decoder == null) {
                return Enum.valueOf((Class<Enum>) keyType.getType(), key);
            } else {
                return decoder.decode(new DecodeContext(opts, null, null, keyType), new ONode(key));
            }
        }

        throw new IllegalArgumentException("Unsupported map key type: " + keyType.getType());
    }

    // 基本类型默认值
    private static void setPrimitiveDefault(Field field, Object bean) throws IllegalAccessException {
        Class<?> type = field.getType();
        if (!type.isPrimitive()) return;

        if (type == int.class) field.setInt(bean, 0);
        else if (type == long.class) field.setLong(bean, 0L);
        else if (type == boolean.class) field.setBoolean(bean, false);
        else if (type == double.class) field.setDouble(bean, 0.0);
        else if (type == float.class) field.setFloat(bean, 0.0f);
        else if (type == short.class) field.setShort(bean, (short) 0);
        else if (type == byte.class) field.setByte(bean, (byte) 0);
        else if (type == char.class) field.setChar(bean, '\u0000');
    }

    private static Object[] getConstructorArguments(Constructor constructor, ONode node, Map<Object, Object> visited, Options opts) throws Exception {
        //只有带参数的构造函（像 java record, kotlin data）
        Set<String> excNames = new LinkedHashSet<>();
        Parameter[] argsP = constructor.getParameters();
        Object[] argsV = new Object[argsP.length];

        for (int j = 0; j < argsP.length; j++) {
            Parameter p = argsP[j];

            //构造参数有的，进入排除
            excNames.add(p.getName());

            if (node.hasKey(p.getName())) {
                ONodeAttr attr = p.getAnnotation(ONodeAttr.class);
                Object val = convertValue(node.get(p.getName()), TypeWrap.from(p.getParameterizedType()), null, attr, visited, opts);
                argsV[j] = val;
            }
        }

        //移除已使用的节点
        for (String excName : excNames) {
            node.remove(excName);
        }

        return argsV;
    }

    /**
     * 确认节点类型
     *
     */
    private static TypeWrap confirmNodeType(Options opts, ONode oRef, TypeWrap def) {
        TypeWrap type0 = getTypeByNode0(opts, oRef, def);

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

    private static TypeWrap getTypeByNode0(Options opts, ONode oRef, TypeWrap def) {
        if (oRef.isObject()) {
            String typeStr = null;
            if (opts.hasFeature(Feature.Read_DisableClassName) == false) {
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
                    throw new SnackException("Unsupported type, class: " + typeStr);
                }

                Class<?> clz = opts.loadClass(typeStr);
                if (clz == null) {
                    throw new SnackException("Unsupported type, class: " + typeStr);
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
}