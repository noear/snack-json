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
package org.noear.snack4.codec.util;

import org.noear.snack4.Feature;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.codec.ObjectEncoder;
import org.noear.snack4.exception.SnackException;
import org.noear.snack4.util.Asserts;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author noear 2025/3/16 created
 */
public class FieldWrap implements Property{
    private final TypeWrap owner;
    private final Field field;
    private final TypeWrap fieldTypeWrap;
    private final ONodeAttr attr;

    private String name;
    private boolean readOnly;
    private boolean asString;
    private boolean flat;

    private boolean serialize = true;
    private boolean deserialize = true;
    private ObjectEncoder serializeEncoder;
    private ObjectDecoder deserializeDecoder;
    private int deserializeFeaturesValue;
    private int serializeFeaturesValue;

    public FieldWrap(TypeWrap owner, Field field) {
        if (field.isAccessible() == false) {
            field.setAccessible(true);
        }

        this.owner = owner;
        this.field = field;
        this.fieldTypeWrap = TypeWrap.from(GenericUtil.reviewType(field.getGenericType(), getGenericInfo(owner, field)));
        this.attr = field.getAnnotation(ONodeAttr.class);
        this.readOnly = Modifier.isFinal(field.getModifiers());

        if (attr != null) {
            name = attr.name();
            asString = attr.asString();
            flat = attr.flat();

            serialize = attr.serialize();
            deserialize = attr.deserialize();

            if (attr.serializeEncoder().isInterface() == false) {
                serializeEncoder = BeanUtil.newInstance(attr.serializeEncoder());
            }

            if (attr.deserializeDecoder().isInterface() == false) {
                deserializeDecoder = BeanUtil.newInstance(attr.deserializeDecoder());
            }

            deserializeFeaturesValue = Feature.addFeature(0, attr.deserializeFeatures());
            serializeFeaturesValue = Feature.addFeature(0, attr.serializeFeatures());
        }

        if (Asserts.isEmpty(name)) {
            name = field.getName();
        }

        if (Modifier.isTransient(field.getModifiers())) {
            serialize = false;
            deserialize = false;
        }
    }

    private static Map<String, Type> getGenericInfo(TypeWrap owner, Field field) {
        if (field.getDeclaringClass() == owner.getType()) {
            return owner.getGenericInfo();
        } else {
            Type superType = GenericUtil.reviewType(owner.getType().getGenericSuperclass(), owner.getGenericInfo());
            return getGenericInfo(TypeWrap.from(superType), field);
        }
    }


    public Object getValue(Object target) throws Exception {
        return field.get(target);
    }

    public void setValue(Object target, Object value) throws Exception {
        if (readOnly == false) {
            field.set(target, value);
        }
    }

    public Field getField() {
        return field;
    }

    public TypeWrap getTypeWrap() {
        return fieldTypeWrap;
    }

    public ONodeAttr getAttr() {
        return attr;
    }

    public String getName() {
        return name;
    }

    public boolean isAsString() {
        return asString;
    }

    public boolean isFlat() {
        return flat;
    }

    /// //////

    public boolean isSerialize() {
        return serialize;
    }

    public boolean isDeserialize() {
        return deserialize;
    }

    public boolean hasSerializeFeature(Feature feature) {
        return Feature.hasFeature(serializeFeaturesValue, feature);
    }

    public boolean hasDeserializeFeature(Feature feature) {
        return Feature.hasFeature(deserializeFeaturesValue, feature);
    }

    public ObjectEncoder getSerializeEncoder() {
        return serializeEncoder;
    }

    public ObjectDecoder getDeserializeDecoder() {
        return deserializeDecoder;
    }

    @Override
    public String toString() {
        return field.toString();
    }

}