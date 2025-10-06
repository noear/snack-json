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
    private boolean serialize = true;
    private boolean deserialize = true;

    private String alias;
    private boolean ignore;
    private ObjectDecoder decoder;
    private ObjectEncoder encoder;

    public FieldWrap(Field field) {
        this.field = field;
        this.attr = field.getAnnotation(ONodeAttr.class);

        field.setAccessible(true);

        if (attr != null) {
            alias = attr.alias();
            ignore = attr.ignore();
            serialize = attr.serialize();
            deserialize = attr.deserialize();

            if (attr.decoder() != ObjectDecoder.class) {
                decoder = ReflectionUtil.newInstance(attr.decoder(), e -> new AnnotationProcessException("Failed to create decoder for field: " + field.getName(), e));
            }

            if (attr.encoder() != ObjectEncoder.class) {
                encoder = ReflectionUtil.newInstance(attr.encoder(), e -> new AnnotationProcessException("Failed to create encoder for field: " + field.getName(), e));
            }
        }

        if (Modifier.isTransient(field.getModifiers())) {
            ignore = true;
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

    public String getAliasName() {
        if (Asserts.isEmpty(alias)) {
            return field.getName();
        } else {
            return alias;
        }
    }


    public ObjectDecoder getDecoder() {
        return decoder;
    }

    public ObjectEncoder getEncoder() {
        return encoder;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public boolean isSerialize() {
        return serialize && !ignore;
    }

    public boolean isDeserialize() {
        return deserialize && !ignore;
    }
}