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
import org.noear.snack4.codec.util.FieldWrap;
import org.noear.snack4.codec.util.ReflectionUtil;
import org.noear.snack4.codec.util.TypeWrap;
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
    // 反序列化：ONode转对象
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

        TypeWrap typeWrap = new TypeWrap(type);

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
        if (node.isObject() || node.isArray()) {
            typeWrap.setClazz(getTypeByNode(opts, node, typeWrap.getClazz()));
        }

        // 处理泛型类型
        if (typeWrap.getType() instanceof ParameterizedType) {
            if (List.class.isAssignableFrom(typeWrap.getClazz())) {
                //将 target 传递给 convertToList
                Type[] typeArgs = typeWrap.getActualTypeArguments();
                return convertToList(node, TypeWrap.from(typeArgs[0]), target, visited, opts);
            } else if (Map.class.isAssignableFrom(typeWrap.getClazz())) {
                //将 target 传递给 convertToMap
                Type[] typeArgs = typeWrap.getActualTypeArguments();
                return convertToMap(node, typeArgs[0], TypeWrap.from(typeArgs[1]), target, visited, opts);
            }
        }

        // 处理嵌套对象 //将 target 传递给 convertNodeToBean
        return convertNodeToBean(node, typeWrap, target, visited, opts);
    }

    @SuppressWarnings("unchecked")
    private static Object convertNodeToBean(ONode node, TypeWrap typeWrap, Object target, Map<Object, Object> visited, Options opts) throws Exception {

        // 优先使用自定义编解码器
        ObjectDecoder decoder = opts.getDecoder(typeWrap.getClazz());
        if (decoder != null) {
            return decoder.decode(opts, null, node, typeWrap.getClazz());
        }

        Object bean = target;

        if (bean == null) {
            // 如果没有传入 target，则执行原有的创建新对象的逻辑
            ObjectFactory factory = opts.getFactory(typeWrap.getClazz());
            if (factory != null) {
                bean = factory.create(opts, typeWrap.getClazz());
            }

            if (bean == null) {
                if (typeWrap.getClazz().isInterface()) {
                    throw new IllegalArgumentException("can not convert bean to type: " + typeWrap.getClazz());
                }

                bean = ReflectionUtil.newInstance(typeWrap.getClazz());
            }
        }

        if (bean instanceof Map) {
            if (node.isNull()) {
                bean = null;
            } else if (node.isObject()) {
                Type valueType = Object.class;
                if (typeWrap.isParameterizedType()) {
                    valueType = typeWrap.getActualTypeArguments()[1];
                }

                Map map = (Map) bean;

                for (Map.Entry<String, ONode> entry : node.getObject().entrySet()) {
                    //填充 Map 时，值为新创建的，所以 target 传 null
                    map.put(entry.getKey(), convertNodeToBean(entry.getValue(), TypeWrap.from(valueType), null, visited, opts));
                }
            } else {
                throw new IllegalArgumentException("The type of node " + node.getType() + " cannot be converted to map.");
            }
        } else if (bean instanceof Collection) {
            Type elementType = Object.class;
            if (typeWrap.isParameterizedType()) {
                elementType = typeWrap.getActualTypeArguments()[0];
            }

            if (node.isNull()) {
                bean = null;
            } else if (node.isArray()) {
                Collection coll = (Collection) bean;

                for (ONode n1 : node.getArray()) {
                    //填充集合时，元素为新创建的，所以 target 传 null
                    Object item = convertNodeToBean(n1, TypeWrap.from(elementType), null, visited, opts);
                    if (item != null) {
                        coll.add(item);
                    }
                }
            }
        } else {
            for (FieldWrap field : ReflectionUtil.getDeclaredFields(typeWrap.getClazz())) {
                ONode fieldNode = node.get(field.getAliasName());

                if (fieldNode != null && !fieldNode.isNull()) {
                    //深度填充：获取字段当前的值，作为递归调用的 target
                    Object existingFieldValue = field.getField().get(bean);
                    Object value = convertValue(fieldNode, field.getTypeWrap(), existingFieldValue, field.getAttr(), visited, opts);
                    field.getField().set(bean, value);
                } else {
                    setPrimitiveDefault(field.getField(), bean);
                }
            }
        }

        return bean;
    }


    //-- 辅助方法 --//
    // 处理List泛型
    private static List<?> convertToList(ONode node, TypeWrap elementTypeWrap, Object target, Map<Object, Object> visited, Options opts) throws Exception {
        List<Object> list = null;
        if (target instanceof List) {
            list = (List<Object>) target;
        } else {
            list = new ArrayList<>();
        }

        for (ONode itemNode : node.getArray()) {
            //列表元素是新对象，递归调用时 target 传 null
            list.add(convertValue(itemNode, elementTypeWrap, null, null, visited, opts));
        }
        return list;
    }

    // 处理Map泛型
    private static Map<?, ?> convertToMap(ONode node, Type keyType, TypeWrap valueTypeWrap, Object target, Map<Object, Object> visited, Options opts) throws Exception {
        Map<Object, Object> map = null;
        if (target instanceof Map) {
            map = (Map<Object, Object>) target;
        } else {
            map = new LinkedHashMap<>();
        }

        for (Map.Entry<String, ONode> kv : node.getObject().entrySet()) {
            //Map 的值是新对象，递归调用时 target 传 null
            Object k = convertKey(kv.getKey(), keyType);
            Object v = convertValue(kv.getValue(), valueTypeWrap, null, null, visited, opts);
            map.put(k, v);
        }

        return map;
    }

    // Map键类型转换
    private static Object convertKey(String key, Type keyType) {
        if (keyType == String.class) return key;
        if (keyType == Integer.class || keyType == int.class) return Integer.parseInt(key);
        if (keyType == Long.class || keyType == long.class) return Long.parseLong(key);
        throw new IllegalArgumentException("Unsupported map key type: " + keyType);
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

    private static Class<?> getTypeByNode(Options opts, ONode oRef, Class<?> def) {
        Class<?> clz0 = getTypeByNode0(opts, oRef, def);

        if (Throwable.class.isAssignableFrom(clz0)) {
            return clz0;
        }

        // 如果自定义了类型，则自定义的类型优先
        if (def != null) {
            if (def != Object.class
                    && def.isInterface() == false
                    && Modifier.isAbstract(def.getModifiers()) == false) {
                return def;
            }
        }

        return clz0;
    }

    private static Class<?> getTypeByNode0(Options opts, ONode oRef, Class<?> def) {
        //
        // 下面使用 .ary(), .oby(), .val() 可以减少检查；从而提高性能
        //
        ONode o = oRef;
        if (def == null) {
            if (o.isObject()) {
                return LinkedHashMap.class;
            }

            if (o.isArray()) {
                return ArrayList.class;
            }
        }

        String typeStr = null;
        if (opts.isFeatureEnabled(Feature.Read_DisableClassName) == false) {
            if (o.isObject()) {
                ONode n1 = o.getObject().get(opts.getTypePropertyName());
                if (n1 != null) {
                    typeStr = n1.getString();
                }
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
                return clz;
            }
        } else {
            if (def == null || def == Object.class) {
                if (o.isObject()) {
                    return LinkedHashMap.class;
                }

                if (o.isArray()) {
                    return ArrayList.class;
                }
            }

            return def;
        }
    }
}