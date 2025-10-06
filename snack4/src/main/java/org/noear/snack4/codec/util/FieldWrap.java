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
import org.noear.snack4.exception.AnnotationProcessException;
import org.noear.snack4.util.Asserts;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author noear 2025/3/16 created
 */
public class FieldWrap {
    private final Field field;
    private final ONodeAttr attr;

    private String name;
    private boolean asString;

    private boolean serialize = true;
    private boolean deserialize = true;
    private ObjectEncoder serializeEncoder;
    private ObjectDecoder deserializeDecoder;
    private int deserializeFeaturesValue;
    private int serializeFeaturesValue;

    public FieldWrap(Field field) {
        this.field = field;
        this.attr = field.getAnnotation(ONodeAttr.class);

        field.setAccessible(true);

        if (attr != null) {
            name = attr.name();
            asString = attr.asString();

            serialize = attr.serialize();
            deserialize = attr.deserialize();

            if (attr.serializeEncoder().isInterface() == false) {
                serializeEncoder = ReflectionUtil.newInstance(attr.serializeEncoder(), e -> new AnnotationProcessException("Failed to create decoder for field: " + field.getName(), e));
            }

            if (attr.deserializeDecoder().isInterface() == false) {
                deserializeDecoder = ReflectionUtil.newInstance(attr.deserializeDecoder(), e -> new AnnotationProcessException("Failed to create encoder for field: " + field.getName(), e));
            }

            deserializeFeaturesValue = Feature.addFeature(0, attr.deserializeFeatures());
            serializeFeaturesValue = Feature.addFeature(0, attr.serializeFeatures());
        }

        if (Modifier.isTransient(field.getModifiers())) {
            serialize = false;
            deserialize = false;
        }
    }

    public Field getField() {
        return field;
    }

    public TypeWrap getTypeWrap() {
        return new TypeWrap(field.getGenericType());
    }

    public ONodeAttr getAttr() {
        return attr;
    }

    public String getName() {
        if (Asserts.isEmpty(name)) {
            return field.getName();
        } else {
            return name;
        }
    }

    public boolean isAsString() {
        return asString;
    }

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
}