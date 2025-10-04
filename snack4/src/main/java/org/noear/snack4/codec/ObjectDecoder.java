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

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.decode.*;
import org.noear.snack4.codec.util.FieldWrapper;
import org.noear.snack4.codec.util.ReflectionUtil;

import java.lang.reflect.*;
import java.util.*;

/**
 * 对象解码器
 *
 * @author noear
 * @since 4.0
 */
public class ObjectDecoder {
    private static NodeDecoder getNodeDecoder(Options opts, Class<?> clazz) {
        return CodecRepository.getNodeDecoder(opts, clazz);
    }

    private static ObjectFactory getObjectFactory(Options opts, Class<?> clazz) {
        return CodecRepository.getObjectFactory(opts, clazz);
    }


    // 反序列化：ONode转对象
    public static <T> T deserialize(ONode node, Type type) {
        return deserialize(node, type, Options.def());
    }

    public static <T> T deserialize(ONode node, Type type, Options opts) {
        if (node == null || type == null) {
            return null;
        }

        if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            if (clazz.isAnonymousClass()) {
                type = clazz.getGenericSuperclass();
            }
        }

        try {
            return (T) convertNodeToBean(node, type, new IdentityHashMap<>(), opts);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Object convertNodeToBean(ONode node, Type type, Map<Object, Object> visited, Options opts) throws Exception {
        Class<?> clazz = null;
        if (type instanceof Class<?>) {
            clazz = (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            clazz = (Class<?>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof GenericArrayType) {
            clazz = (Class<?>) ((GenericArrayType) type).getGenericComponentType();
        } else if (type instanceof TypeVariable) {
            clazz = (Class<?>) ((TypeVariable) type).getBounds()[0];
        }

        if (clazz == null) {
            throw new IllegalArgumentException("can not convert bean to type: " + type);
        }

        // 优先使用自定义编解码器
        NodeDecoder decoder = getNodeDecoder(opts, clazz);
        if (decoder != null) {
            return decoder.decode(opts, null, node, clazz);
        }

        if (clazz.isArray()) {
            return _ArrayDecoder.instance().decode(opts, null, node, clazz);
        } else if (clazz.isEnum()) {
            return _EnumDecode.getInstance().decode(opts, null, node, clazz);
        }

        Object bean = null;

        ObjectFactory factory = getObjectFactory(opts, clazz);
        if (factory != null) {
            bean = factory.create(opts, clazz);
        }

        if (bean == null) {
            if (clazz.isInterface()) {
                throw new IllegalArgumentException("can not convert bean to type: " + clazz);
            }

            bean = ReflectionUtil.newInstance(clazz);
        }

        if (bean instanceof Map) {
            if (node.isNull()) {
                bean = null;
            } else if (node.isObject()) {
                Type itemType = Object.class;
                if (type instanceof ParameterizedType) {
                    itemType = ((ParameterizedType) type).getActualTypeArguments()[1];
                }

                Map map = (Map) bean;

                for (Map.Entry<String, ONode> entry : node.getObject().entrySet()) {
                    map.put(entry.getKey(), convertNodeToBean(entry.getValue(), itemType, visited, opts));
                }
            } else {
                throw new IllegalArgumentException("The type of node " + node.getType() + " cannot be converted to map.");
            }
        } else if (bean instanceof Collection) {
            Type itemType = Object.class;
            if (type instanceof ParameterizedType) {
                itemType = ((ParameterizedType) type).getActualTypeArguments()[0];
            }

            if (node.isNull()) {
                bean = null;
            } else if (node.isArray()) {
                if (Set.class.isAssignableFrom(clazz)) {
                    Set tmp = (Set) bean;

                    for (ONode n1 : node.getArray()) {
                        Object item = convertNodeToBean(n1, itemType, visited, opts);
                        if (item != null) {
                            tmp.add(item);
                        }
                    }
                } else {
                    List tmp = (List) bean;

                    for (ONode n1 : node.getArray()) {
                        Object item = convertNodeToBean(n1, itemType, visited, opts);
                        if (item != null) {
                            tmp.add(item);
                        }
                    }
                }
            }
        } else {
            for (FieldWrapper field : ReflectionUtil.getDeclaredFields(clazz)) {
                ONode fieldNode = node.get(field.getAliasName());

                if (fieldNode != null && !fieldNode.isNull()) {
                    Object value = convertValue(fieldNode, field.getField().getGenericType(), field.getAttr(), visited, opts);
                    field.getField().set(bean, value);
                } else {
                    setPrimitiveDefault(field.getField(), bean);
                }
            }
        }

        return bean;
    }


    // 类型转换核心
    private static Object convertValue(ONode node, Type type, ONodeAttr attr, Map<Object, Object> visited, Options opts) throws Exception {
        if (node.isNull()) {
            return null;
        }

        // 处理泛型类型
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Type rawType = pType.getRawType();

            if (List.class.isAssignableFrom((Class<?>) rawType)) {
                return convertToList(node, pType.getActualTypeArguments()[0], visited, opts);
            } else if (Map.class.isAssignableFrom((Class<?>) rawType)) {
                Type[] typeArgs = pType.getActualTypeArguments();
                return convertToMap(node, typeArgs[0], typeArgs[1], visited, opts);
            }
        }

        // 处理基本类型
        Class<?> clazz = (Class<?>) (type instanceof Class ? type : ((ParameterizedType) type).getRawType());

        // 优先使用自定义编解码器
        NodeDecoder decoder = getNodeDecoder(opts, clazz);
        if (decoder != null) {
            return decoder.decode(opts, attr, node, clazz);
        }

        if (clazz.isArray()) {
            return _ArrayDecoder.instance().decode(opts, null, node, clazz);
        } else if (clazz.isEnum()) {
            return _EnumDecode.getInstance().decode(opts, null, node, clazz);
        } else if (clazz == Object.class) {
            return node.getValue();
        }

        // 处理嵌套对象
        return convertNodeToBean(node, clazz, visited, opts);
    }

    //-- 辅助方法 --//
    // 处理List泛型
    private static List<?> convertToList(ONode node, Type elementType, Map<Object, Object> visited, Options opts) throws Exception {
        List<Object> list = new ArrayList<>();
        for (ONode itemNode : node.getArray()) {
            list.add(convertValue(itemNode, elementType, null, visited, opts));
        }
        return list;
    }

    // 处理Map泛型
    private static Map<?, ?> convertToMap(ONode node, Type keyType, Type valueType, Map<Object, Object> visited, Options opts) throws Exception {
        Map<Object, Object> map = new LinkedHashMap<>();

        for (Map.Entry<String, ONode> kv : node.getObject().entrySet()) {
            Object k = convertKey(kv.getKey(), keyType);
            Object v = convertValue(kv.getValue(), valueType, null, visited, opts);
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
}