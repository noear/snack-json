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
package org.noear.snack4;

import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.codec.ObjectEncoder;
import org.noear.snack4.json.JsonReader;
import org.noear.snack4.json.JsonSource;
import org.noear.snack4.json.JsonType;
import org.noear.snack4.json.JsonWriter;
import org.noear.snack4.jsonpath.JsonPathProvider;

import java.io.StringReader;
import java.util.*;
import java.util.function.Consumer;

/**
 * 高性能 JSON 节点抽象
 */
public final class ONode {
    private static JsonPathProvider jsonPathProvider;

    static {
        ServiceLoader<JsonPathProvider> serviceLoader = ServiceLoader.load(JsonPathProvider.class);
        for (JsonPathProvider provider : serviceLoader) {
            jsonPathProvider = provider;
        }
    }

    private Object value;
    private JsonType type;

    public transient JsonSource source;

    public ONode() {
        this.type = JsonType.TYPE_NULL;
    }

    public ONode(Object value) {
        this.value = value;
        this.type = JsonType.resolveType(value);
    }

    // Getters and Setters
    public boolean isNull() {
        return type == JsonType.TYPE_NULL;
    }

    public boolean isNullOrEmpty() {
        return type == JsonType.TYPE_NULL ||
                (type == JsonType.TYPE_OBJECT && getObject().isEmpty()) ||
                (type == JsonType.TYPE_ARRAY && getArray().isEmpty());
    }

    public boolean isBoolean() {
        return type == JsonType.TYPE_BOOLEAN;
    }

    public boolean isNumber() {
        return type == JsonType.TYPE_NUMBER;
    }

    public boolean isString() {
        return type == JsonType.TYPE_STRING;
    }

    public boolean isDate() {
        return type == JsonType.TYPE_DATE;
    }

    public boolean isArray() {
        return type == JsonType.TYPE_ARRAY;
    }

    public boolean isObject() {
        return type == JsonType.TYPE_OBJECT;
    }

    public boolean isValue() {
        return JsonType.isValue(type);
    }

    public Object getValue() {
        return value;
    }

    public Boolean getBoolean() {
        return (Boolean) value;
    }

    public Number getNumber() {
        return (Number) value;
    }

    public Number getNumber(Number def) {
        if (value == null) {
            return def;
        } else {
            return (Number) value;
        }
    }

    public String getString() {
        return (String) value;
    }

    public Date getDate() {
        return (Date) value;
    }

    @SuppressWarnings("unchecked")
    public List<ONode> getArray() {
        return (List<ONode>) value;
    }

    @SuppressWarnings("unchecked")
    public Map<String, ONode> getObject() {
        return (Map<String, ONode>) value;
    }

    public ONode newObject() {
        if (value == null) {
            value = new LinkedHashMap<>();
            type = JsonType.TYPE_OBJECT;
        }

        return this;
    }

    public ONode newArray() {
        if (value == null) {
            value = new ArrayList<>();
            type = JsonType.TYPE_ARRAY;
        }

        return this;
    }

    public int getInt() {
        return getNumber(0).intValue();
    }

    public long getLong() {
        return getNumber(0L).longValue();
    }

    public double getDouble() {
        return getNumber(0D).doubleValue();
    }

    public ONode get(String key) {
        return getObject().get(key);
    }

    public ONode getOrNew(String key) {
        return getObject().computeIfAbsent(key, k -> new ONode());
    }

    public void remove(String key) {
        getObject().remove(key);
    }

    public void setValue(Object value) {
        this.value = value;
        this.type = JsonType.resolveType(value);
    }

    public ONode set(String key, Object value) {
        ONode oNode;
        if (value instanceof ONode) {
            oNode = (ONode) value;
        } else if (value instanceof Collection) {
            oNode = ObjectEncoder.serialize(value);
        } else if (value instanceof Map) {
            oNode = ObjectEncoder.serialize(value);
        } else {
            oNode = new ONode(value);
        }

        return set0(key, oNode);
    }

    private ONode set0(String key, ONode value) {
        if (type == JsonType.TYPE_NULL) {
            newObject();
        }

        getObject().put(key, value);
        return this;
    }

    public ONode get(int index) {
        if (index < 0) {
            int pos = getArray().size() + index;
            return getArray().get(pos);
        } else {
            return getArray().get(index);
        }
    }

    public void remove(int index) {
        if (index < 0) {
            int pos = getArray().size() + index;
            getArray().remove(pos);
        } else {
            getArray().remove(index);
        }
    }

    public ONode add(Object value) {
        ONode oNode;
        if (value instanceof ONode) {
            oNode = (ONode) value;
        } else if (value instanceof Collection) {
            oNode = ObjectEncoder.serialize(value);
        } else if (value instanceof Map) {
            oNode = ObjectEncoder.serialize(value);
        } else {
            oNode = new ONode(value);
        }

        add0(oNode);
        return this;
    }

    public ONode addNew() {
        newArray();

        ONode oNode = new ONode();
        getArray().add(oNode);
        return oNode;
    }

    private ONode add0(ONode value) {
        if (type == JsonType.TYPE_NULL) {
            newArray();
        }

        getArray().add(value);
        return this;
    }

    public ONode then(Consumer<ONode> builder) {
        builder.accept(this);
        return this;
    }

    public Optional<ONode> getOptional(String key) {
        return isObject() ? Optional.ofNullable(getObject().get(key)) : Optional.empty();
    }

    public Optional<ONode> getOptional(int index) {
        if (!isArray()) return Optional.empty();
        List<ONode> arr = getArray();
        return (index >= 0 && index < arr.size()) ? Optional.of(arr.get(index)) : Optional.empty();
    }

    public int size() {
        if (isArray()) return getArray().size();
        if (isObject()) return getObject().size();
        return 1;
    }

    public ONode reset(Object value) {
        this.value = value;
        this.type = JsonType.resolveType(value);
        return this;
    }

    public void clear() {
        if (isObject()) {
            ((Map<?, ?>) value).clear();
        } else if (isArray()) {
            ((List<?>) value).clear();
        } else {
            reset(null);
        }
    }

    public JsonType getType() {
        return type;
    }

    public boolean hasKey(String key) {
        return isObject() && getObject().containsKey(key);
    }

    /// /////////////

    /**
     * 根据 jsonpath 查询
     */
    public ONode select(String jsonpath) {
        Objects.requireNonNull(jsonPathProvider, "Requires 'snack4-jsonpath' dependency ");
        return jsonPathProvider.select(this, jsonpath);
    }

    /**
     * 根据 jsonpath 删除
     */
    public void delete(String jsonpath) {
        Objects.requireNonNull(jsonPathProvider, "Requires 'snack4-jsonpath' dependency ");
        jsonPathProvider.delete(this, jsonpath);
    }

    /**
     * 根据 jsonpath 生成
     */
    public ONode create(String jsonpath) {
        Objects.requireNonNull(jsonPathProvider, "Requires 'snack4-jsonpath' dependency ");
        return jsonPathProvider.create(this, jsonpath);
    }


    /// /////////////

    public static ONode from(Object bean, Options opts) {
        return ObjectEncoder.serialize(bean, opts);
    }

    public static ONode from(Object bean) {
        return ObjectEncoder.serialize(bean, Options.def());
    }

    // 添加带 Options 的静态方法
    public static ONode load(String json, Options opts) {
        try {
            return new JsonReader(new StringReader(json), opts).read();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }


    // 保持原有方法兼容性
    public static ONode load(String json) {
        return load(json, Options.def());
    }


    public <T> T toBean(Class<T> clazz, Options opts) {
        return ObjectDecoder.deserialize(this, clazz, opts);
    }

    public <T> T toBean(Class<T> clazz) {
        return toBean(clazz, Options.def());
    }

    public String toJson(Options opts) {
        try {
            return JsonWriter.serialize(this, opts);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    public String toJson() {
        return toJson(Options.def());
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}