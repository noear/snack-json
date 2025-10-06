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
import org.noear.snack4.codec.util.FieldWrapper;
import org.noear.snack4.codec.util.ReflectionUtil;

import java.lang.reflect.Array;
import java.util.*;

/**
 * 对象编码器
 *
 * @author noear
 * @since 4.0
 */
public class BeanSerializer {
    // 序列化：对象转ONode
    public static ONode serialize(Object value) {
        return serialize(value, null);
    }

    public static ONode serialize(Object value, Options opts) {
        if (value == null) {
            return new ONode(null);
        }

        if (value instanceof ONode) {
            return (ONode) value;
        }

        if (opts == null) {
            opts = Options.def();
        }

        try {
            return convertValueToNode(value, null, new IdentityHashMap<>(), opts);
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
    private static ONode convertValueToNode(Object value, ONodeAttr attr, Map<Object, Object> visited, Options opts) throws Exception {
        if (value == null) {
            return new ONode(null);
        }

        if (value instanceof ONode) {
            return (ONode) value;
        }

        if (value instanceof ObjectEncoder) {
            return ((ObjectEncoder) value).encode(opts, null, value);
        }

        // 优先使用自定义编解码器
        ObjectEncoder codec = opts.getEncoder(value);
        if (codec != null) {
            return codec.encode(opts, attr, value);
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
            throw new StackOverflowError("Circular reference detected: " + bean.getClass().getName());
        } else {
            visited.put(bean, null);
        }

        ONode tmp = new ONode().asObject();

        if (opts.isFeatureEnabled(Feature.Write_ClassName)) {
            tmp.set(opts.getTypePropertyName(), bean.getClass().getName());
        }

        for (FieldWrapper field : ReflectionUtil.getDeclaredFields(bean.getClass())) {
            ONode fieldNode = convertValueToNode(field.getField().get(bean), field.getAttr(), visited, opts);
            tmp.set(field.getAliasName(), fieldNode);
        }
        return tmp;
    }

    // 处理数组类型
    private static ONode convertArrayToNode(Object array, Map<Object, Object> visited, Options opts) throws Exception {
        ONode tmp = new ONode().asArray();
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            tmp.add(convertValueToNode(Array.get(array, i), null, visited, opts));
        }
        return tmp;
    }

    // 处理集合类型
    private static ONode convertCollectionToNode(Collection<?> collection, Map<Object, Object> visited, Options opts) throws Exception {
        ONode tmp = new ONode().asArray();
        for (Object item : collection) {
            tmp.add(convertValueToNode(item, null, visited, opts));
        }
        return tmp;
    }

    // 处理Map类型
    private static ONode convertMapToNode(Map<?, ?> map, Map<Object, Object> visited, Options opts) throws Exception {
        ONode tmp = new ONode().asObject();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            ONode valueNode = convertValueToNode(entry.getValue(), null, visited, opts);
            tmp.set(String.valueOf(entry.getKey()), valueNode);
        }
        return tmp;
    }
}