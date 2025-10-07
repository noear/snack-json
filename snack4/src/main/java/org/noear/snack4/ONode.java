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
        return isNull() ||
                (isString() && Asserts.isEmpty(getString())) ||
                (isObject() && Asserts.isEmpty(getObject())) ||
                (isArray() && Asserts.isEmpty(getArray())) ;
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
        if (isBoolean()) {
            return (Boolean) value;
        } else if (isString()) {
            return Boolean.parseBoolean((String) value);
        } else if (isNumber()) {
            return getNumber().longValue() > 0;
        } else {
            throw new TypeConvertException("Not supported for automatic conversion");
        }
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
            value = new LinkedHashMap<String, ONode>();
            type = JsonType.Object;
        }

        return this;
    }

    public ONode asArray() {
        if (value == null) {
            value = new ArrayList<ONode>();
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

    public Integer getInt() {
        return getInt(0);
    }

    public Integer getInt(Integer def) {
        if (isNumber()) {
            return getNumber().intValue();
        } else if (isNullOrEmpty()) {
            return def;
        } else if (isString()) {
            return Integer.parseInt(getString());
        } else {
            throw new TypeConvertException("Not supported for automatic conversion");
        }
    }

    public Long getLong() {
        return getLong(0L);
    }

    public Long getLong(Long def) {
        if (isNumber()) {
            return getNumber().longValue();
        } else if (isNullOrEmpty()) {
            return def;
        } else if (isString()) {
            return Long.getLong(getString());
        } else {
            throw new TypeConvertException("Not supported for automatic conversion");
        }
    }

    public Float getFloat() {
        return getFloat(0F);
    }

    public Float getFloat(Float def) {
        if (isNumber()) {
            return getNumber().floatValue();
        } else if (isNullOrEmpty()) {
            return def;
        } else if (isString()) {
            return Float.parseFloat(getString());
        } else {
            throw new TypeConvertException("Not supported for automatic conversion");
        }
    }

    public Double getDouble() {
        return getDouble(0D);
    }

    public Double getDouble(Double def) {
        if (isNumber()) {
            return getNumber().doubleValue();
        } else if (isNullOrEmpty()) {
            return def;
        } else if (isString()) {
            return Double.parseDouble(getString());
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
        return fillJson(json, Options.of(features));
    }

    public ONode fillJson(String json, Options opts) {
        return this.fill(ONode.load(json, opts), opts);
    }

    public ONode setAll(Map<?,?> map) {
        for (Map.Entry entry : map.entrySet()) {
            set(String.valueOf(entry.getKey()), entry.getValue());
        }

        return this;
    }

    public ONode set(String key, Object value) {
        ONode oNode;
        if (value == null) {
            oNode = new ONode(null);
        } else if (value instanceof ONode) {
            oNode = (ONode) value;
        } else if (value instanceof Collection) {
            oNode = BeanSerializer.serialize(value);
        } else if (value instanceof Map) {
            oNode = BeanSerializer.serialize(value);
        } else {
            if (value.getClass().isArray()) {
                oNode = new ONode().addAll(Arrays.asList((Object[]) value));
            } else {
                oNode = new ONode(value);
            }
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
                if(thenApply != null) {
                    thenApply.accept(last);
                }
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

    public void clear() {
        if (isObject()) {
            ((Map<?, ?>) value).clear();
        } else if (isArray()) {
            ((List<?>) value).clear();
        } else {
            this.value = null;
            this.type = JsonType.Null;
        }
    }

    public JsonType getType() {
        return type;
    }

    public boolean hasKey(String key) {
        return isObject() && getObject().containsKey(key);
    }

    public boolean hasValue(Object value) {
        if (isObject()) {
            for (ONode n : getObject().values()) {
                if (n.equals(value)) {
                    return true;
                }
            }
            return false;
        } else if (isArray()) {
            for (ONode n : getArray()) {
                if (n.equals(value)) {
                    return true;
                }
            }
            return false;
        } else if (isValue()) {
            return getValue().equals(value);
        } else {
            return false;
        }
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
        return false == select(jsonpath).isUndefined();
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
        return BeanSerializer.serialize(bean, opts);
    }

    public static ONode from(Object bean, Feature... features) {
        if (Asserts.isEmpty(features)) {
            return BeanSerializer.serialize(bean, Options.def());
        } else {
            return BeanSerializer.serialize(bean, Options.of(features));
        }
    }

    public static ONode load(String json, Feature... features) {
        if (Asserts.isEmpty(features)) {
            return load(json, Options.def());
        } else {
            return load(json, Options.of(features));
        }
    }

    public static ONode load(String json, Options opts) {
        try {
            return new JsonReader(new StringReader(json), opts).read();
        } catch (SnackException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new SnackException(ex);
        }
    }


    public static String serialize(Object object, Feature... features) {
        if (Asserts.isEmpty(features)) {
            return serialize(object, Options.def());
        } else {
            return serialize(object, Options.of(features));
        }
    }

    public static String serialize(Object object, Options opts) {
        return ONode.from(object, opts).toJson(opts);
    }

    public static <T> T deserialize(String json, Feature... features) {
        if (Asserts.isEmpty(features)) {
            return deserialize(json, Object.class, Options.def());
        } else {
            return deserialize(json, Object.class, Options.of(features));
        }
    }

    public static <T> T deserialize(String json, Type type, Feature... features) {
        if (Asserts.isEmpty(features)) {
            return deserialize(json, type, Options.def());
        } else {
            return deserialize(json, type, Options.of(features));
        }
    }

    public static <T> T deserialize(String json, Type type, Options opts) {
        return ONode.load(json, opts).to(type, opts);
    }

    public static <T> T deserialize(String json, TypeRef<T> type, Feature... features) {
        if (Asserts.isEmpty(features)) {
            return deserialize(json, type, Options.def());
        } else {
            return deserialize(json, type, Options.of(features));
        }
    }

    public static <T> T deserialize(String json, TypeRef<T> type, Options opts) {
        return ONode.load(json, opts).to(type, opts);
    }

    /// ///////////

    public <T> T to(Type type, Options opts) {
        return BeanDeserializer.deserialize(this, type, null, opts);
    }

    public <T> T to(Type type, Feature... features) {
        if (Asserts.isEmpty(features)) {
            return to(type, Options.def());
        } else {
            return to(type, Options.of(features));
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
            return toJson(Options.of(features));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return isNull();
        }

        if (isArray()) {
            if (o instanceof ONode) {
                ONode o1 = (ONode) o;
                return o1.isArray() && Objects.equals(getArray(), o1.getArray());
            } else {
                return Objects.equals(getArray(), o);
            }
        }

        if (isObject()) {
            if (o instanceof ONode) {
                ONode o1 = (ONode) o;
                return o1.isObject() && Objects.equals(getObject(), ((ONode) o).getObject());
            } else {
                return Objects.equals(getObject(), o);
            }
        }

        if (isValue()) {
            if (o instanceof ONode) {
                ONode o1 = (ONode) o;
                return o1.isValue() && Objects.equals(getValue(), ((ONode) o).getValue());
            } else {
                return Objects.equals(getValue(), o);
            }
        }

        //最后是null type
        if (o instanceof ONode) {
            return ((ONode) o).isNull(); //都是 null
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (isNull()) {
            return 0;
        } else {
            return value.hashCode();
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
        if (source == null) {
            return null;
        } else {
            return source.parent;
        }
    }

    @Deprecated
    public ONode parents(int depth) {
        if (source == null) {
            return null;
        } else {
            ONode tmp = this;
            while (depth > 0) {
                if (tmp == null) {
                    break;
                } else {
                    tmp = tmp.parent();
                }

                depth--;
            }

            return tmp;
        }
    }

    @Deprecated
    public List<String> pathList() {
        List<String> paths = new ArrayList<>();
        extractPath(paths, this);
        return paths;
    }

    public String path() {
        if (source == null) {
            return null;
        } else {
            Object pk = (source.key == null ? source.index : source.key);
            String pp = source.parent.path();

            if (pp == null) {
                return "$[" + pk + "]";
            } else {
                return pp + "[" + pk + "]";
            }
        }
    }

    public static void extractPath(List<String> paths, ONode oNode) {
        String path = oNode.path();
        if (path != null) {
            paths.add(path);
        }

        if (oNode.isArray()) {
            for (ONode n1 : oNode.getArray()) {
                extractPath(paths, n1);
            }
        } else if (oNode.isObject()) {
            for (Map.Entry<String, ONode> kv : oNode.getObject().entrySet()) {
                extractPath(paths, kv.getValue());
            }
        }
    }
}