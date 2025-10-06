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

import org.noear.snack4.codec.BeanDeserializer;
import org.noear.snack4.codec.BeanSerializer;
import org.noear.snack4.codec.TypeRef;
import org.noear.snack4.codec.util.DateUtil;
import org.noear.snack4.exception.SnackException;
import org.noear.snack4.exception.TypeConvertException;
import org.noear.snack4.json.JsonReader;
import org.noear.snack4.json.JsonSource;
import org.noear.snack4.json.JsonType;
import org.noear.snack4.json.JsonWriter;
import org.noear.snack4.jsonpath.JsonPathProvider;
import org.noear.snack4.util.Asserts;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.text.ParseException;
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
        this.type = JsonType.Undefined;
    }

    public ONode(Object value) {
        this.value = value;
        this.type = JsonType.resolveType(value);
    }

    public JsonType nodeType() {
        return type;
    }

    // Getters and Setters
    public boolean isNull() {
        return type == JsonType.Null || isUndefined();
    }

    public boolean isUndefined() {
        return type == JsonType.Undefined;
    }

    public boolean isNullOrEmpty() {
        return type == JsonType.Null ||
                (type == JsonType.Object && getObject().isEmpty()) ||
                (type == JsonType.Array && getArray().isEmpty());
    }

    public boolean isBoolean() {
        return type == JsonType.Boolean;
    }

    public boolean isNumber() {
        return type == JsonType.Number;
    }

    public boolean isString() {
        return type == JsonType.String;
    }

    public boolean isDate() {
        return type == JsonType.Date;
    }

    public boolean isArray() {
        return type == JsonType.Array;
    }

    public boolean isObject() {
        return type == JsonType.Object;
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
        if (isString()) {
            return (String) value;
        } else if (isNull()) {
            return null;
        } else {
            return String.valueOf(value);
        }
    }

    public Date getDate() {
        if (isDate()) {
            return (Date) value;
        } else if (isNumber()) {
            return new Date(getNumber().longValue());
        } else if (isString()) {
            try {
                return DateUtil.parse(getString());
            } catch (ParseException ex) {
                throw new TypeConvertException(ex);
            }
        } else {
            throw new TypeConvertException("Not supported for automatic conversion");
        }
    }

    @SuppressWarnings("unchecked")
    public List<ONode> getArray() {
        asArray();

        return (List<ONode>) value;
    }

    @SuppressWarnings("unchecked")
    public Map<String, ONode> getObject() {
        asObject();

        return (Map<String, ONode>) value;
    }

    public ONode asObject() {
        if (value == null) {
            value = new LinkedHashMap<>();
            type = JsonType.Object;
        }

        return this;
    }

    public ONode asArray() {
        if (value == null) {
            value = new ArrayList<>();
            type = JsonType.Array;
        }

        return this;
    }

    /**
     * 重命名
     *
     */
    public ONode rename(String oldName, String newName) {
        ONode tmp = remove(oldName);
        getObject().put(newName, tmp);
        return this;
    }

    public int getInt() {
        if (isNumber()) {
            return getNumber(0).intValue();
        } else if (isString()) {
            return Integer.parseInt(getString());
        } else if (isNull()) {
            return 0;
        } else {
            throw new TypeConvertException("Not supported for automatic conversion");
        }
    }

    public long getLong() {
        if (isNumber()) {
            return getNumber(0L).longValue();
        } else if (isString()) {
            return Long.getLong(getString());
        } else if (isNull()) {
            return 0L;
        } else {
            throw new TypeConvertException("Not supported for automatic conversion");
        }
    }

    public double getDouble() {
        if (isNumber()) {
            return getNumber(0D).doubleValue();
        } else if (isString()) {
            return Double.parseDouble(getString());
        } else if (isNull()) {
            return 0D;
        } else {
            throw new TypeConvertException("Not supported for automatic conversion");
        }
    }

    public ONode get(String key) {
        asObject();

        ONode tmp = getObject().get(key);
        if (tmp == null) {
            return new ONode();
        } else {
            return tmp;
        }
    }

    public ONode getOrNew(String key) {
        return getObject().computeIfAbsent(key, k -> new ONode());
    }

    public ONode getOrNull(String key) {
        if (isObject()) {
            return getObject().get(key);
        } else {
            return null;
        }
    }

    public ONode remove(String key) {
        return getObject().remove(key);
    }

    public ONode setValue(Object value) {
        this.value = value;
        this.type = JsonType.resolveValueType(value);
        return this;
    }

    public ONode setValue(Number value) {
        this.value = value;
        this.type = JsonType.Number;
        return this;
    }

    public ONode setValue(Boolean value) {
        this.value = value;
        this.type = JsonType.Boolean;
        return this;
    }

    public ONode setValue(String value) {
        this.value = value;
        this.type = JsonType.String;
        return this;
    }

    public ONode setValue(Date value) {
        this.value = value;
        this.type = JsonType.Date;
        return this;
    }

    public ONode fill(Object source, Feature... features) {
        ONode oNode = ONode.from(source, features);

        this.value = oNode.value;
        this.type = oNode.type;
        return this;
    }

    public ONode fill(Object source, Options opts) {
        ONode oNode = ONode.from(source, opts);

        this.value = oNode.value;
        this.type = oNode.type;
        return this;
    }

    public ONode fillJson(String json, Feature... features) {
        return fillJson(json, Options.enableOf(features));
    }

    public ONode fillJson(String json, Options opts) {
        return this.fill(ONode.fromJson(json, opts), opts);
    }

    public ONode set(String key, Object value) {
        ONode oNode;
        if (value instanceof ONode) {
            oNode = (ONode) value;
        } else if (value instanceof Collection) {
            oNode = BeanSerializer.serialize(value);
        } else if (value instanceof Map) {
            oNode = BeanSerializer.serialize(value);
        } else {
            oNode = new ONode(value);
        }

        return set0(key, oNode);
    }

    private ONode set0(String key, ONode value) {
        if (type == JsonType.Null) {
            asObject();
        }

        getObject().put(key, value);
        return this;
    }

    public ONode get(int index) {
        asArray();

        if (index >= 0 && getArray().size() > index) {
            return getArray().get(index);
        }

        return new ONode();
    }

    public ONode getOrNew(int index) {
        return getOrNew(index, null);
    }

    public ONode getOrNew(int index, Consumer<ONode> thenApply) {
        List<ONode> self = getArray();

        if (self.size() > index) {
            return self.get(index);
        } else {
            ONode last = null;
            for (int i = self.size(); i <= index; i++) {
                last = new ONode();
                thenApply.accept(last);
                self.add(last);
            }

            return last;
        }
    }

    public ONode getOrNull(int index) {
        if (isArray()) {
            if (index >= 0 && getArray().size() > index) {
                return getArray().get(index);
            }
        }

        return null;
    }

    public ONode remove(int index) {
        if (index < 0) {
            int pos = getArray().size() + index;
            return getArray().remove(pos);
        } else {
            return getArray().remove(index);
        }
    }

    public ONode add(Object value) {
        ONode oNode;
        if (value instanceof ONode) {
            oNode = (ONode) value;
        } else if (value instanceof Collection) {
            oNode = BeanSerializer.serialize(value);
        } else if (value instanceof Map) {
            oNode = BeanSerializer.serialize(value);
        } else {
            oNode = new ONode(value);
        }

        add0(oNode);
        return this;
    }

    public ONode addAll(Collection collection) {
        for (Object o : collection) {
            add(o);
        }
        return this;
    }

    public ONode addNew() {
        asArray();

        ONode oNode = new ONode();
        getArray().add(oNode);
        return oNode;
    }

    private ONode add0(ONode value) {
        if (type == JsonType.Null) {
            asArray();
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

    public boolean exists(String jsonpath) {
        return false == select(jsonpath).isNull();
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


    public static String toJson(Object object, Feature... features) {
        if (Asserts.isEmpty(features)) {
            return toJson(object, Options.def());
        } else {
            return toJson(object, Options.enableOf(features));
        }
    }

    public static String toJson(Object object, Options opts) {
        return ONode.from(object, opts).toJson(opts);
    }

    public static ONode from(Object bean, Options opts) {
        return BeanSerializer.serialize(bean, opts);
    }

    public static ONode from(Object bean, Feature... features) {
        if (Asserts.isEmpty(features)) {
            return BeanSerializer.serialize(bean, Options.def());
        } else {
            return BeanSerializer.serialize(bean, Options.enableOf(features));
        }
    }

    public static ONode fromJson(String json, Feature... features) {
        if (Asserts.isEmpty(features)) {
            return fromJson(json, Options.def());
        } else {
            return fromJson(json, Options.enableOf(features));
        }
    }

    public static ONode fromJson(String json, Options opts) {
        try {
            return new JsonReader(new StringReader(json), opts).read();
        } catch (SnackException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new SnackException(ex);
        }
    }


    public static <T> T fromJson(String json, Type type, Feature... features) {
        if (Asserts.isEmpty(features)) {
            return fromJson(json, type, Options.def());
        } else {
            return fromJson(json, type, Options.enableOf(features));
        }
    }

    public static <T> T fromJson(String json, Type type, Options opts) {
        return ONode.fromJson(json, opts).to(type, opts);
    }

    public static <T> T fromJson(String json, TypeRef<T> type, Feature... features) {
        if (Asserts.isEmpty(features)) {
            return fromJson(json, type, Options.def());
        } else {
            return fromJson(json, type, Options.enableOf(features));
        }
    }

    public static <T> T fromJson(String json, TypeRef<T> type, Options opts) {
        return ONode.fromJson(json, opts).to(type, opts);
    }

    /// ///////////

    public <T> T to(Type type, Options opts) {
        return BeanDeserializer.deserialize(this, type, null, opts);
    }

    public <T> T to(Type type, Feature... features) {
        if (Asserts.isEmpty(features)) {
            return to(type, Options.def());
        } else {
            return to(type, Options.enableOf(features));
        }
    }

    public <T> T to(TypeRef<T> typeRef, Feature... features) {
        return to(typeRef.getType(), features);
    }

    public <T> T to(TypeRef<T> typeRef, Options opts) {
        return to(typeRef.getType(), opts);
    }

    public <T> T to(Feature... features) {
        return to(Object.class, features);
    }

    public <T> T bindTo(T target) {
        return BeanDeserializer.deserialize(this, target.getClass(), target, Options.def());
    }

    public <T> T bindTo(T target, Options opts) {
        return BeanDeserializer.deserialize(this, target.getClass(), target, opts);
    }

    public String toJson(Options opts) {
        try {
            return JsonWriter.write(this, opts);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    public String toJson(Feature... features) {
        if (Asserts.isEmpty(features)) {
            return toJson(Options.def());
        } else {
            return toJson(Options.enableOf(features));
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /// ///////////

    @Deprecated
    public ONode usePaths() {
        return this;
    }

    @Deprecated
    public ONode parent() {
        return this;
    }

    @Deprecated
    public ONode parents(int idx) {
        return this;
    }

    @Deprecated
    public List<String> pathList() {
        return null;
    }
}