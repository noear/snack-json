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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author noear 2025/3/16 created
 */
public class FieldWrap {
    private final TypeWrap owner;
    private final Field field;
    private final TypeWrap fieldTypeWrap;
    private final ONodeAttr attr;

    private String name;
    private boolean asString;

    private Method _setter;
    private Method _getter;

    private boolean serialize = true;
    private boolean deserialize = true;
    private ObjectEncoder serializeEncoder;
    private ObjectDecoder deserializeDecoder;
    private int deserializeFeaturesValue;
    private int serializeFeaturesValue;

    public FieldWrap(TypeWrap owner, Field field) {
        this.owner = owner;
        this.field = field;
        this.fieldTypeWrap = TypeWrap.from(field.getGenericType());
        this.attr = field.getAnnotation(ONodeAttr.class);

        field.setAccessible(true);

        if (attr != null) {
            name = attr.name();
            asString = attr.asString();

            serialize = attr.serialize();
            deserialize = attr.deserialize();

            if (attr.serializeEncoder().isInterface() == false) {
                serializeEncoder = ClassUtil.newInstance(attr.serializeEncoder(), e -> new AnnotationProcessException("Failed to create decoder for field: " + field.getName(), e));
            }

            if (attr.deserializeDecoder().isInterface() == false) {
                deserializeDecoder = ClassUtil.newInstance(attr.deserializeDecoder(), e -> new AnnotationProcessException("Failed to create encoder for field: " + field.getName(), e));
            }

            deserializeFeaturesValue = Feature.addFeature(0, attr.deserializeFeatures());
            serializeFeaturesValue = Feature.addFeature(0, attr.serializeFeatures());
        }

        if (Modifier.isTransient(field.getModifiers())) {
            serialize = false;
            deserialize = false;
        }

        _setter = doFindSetter(field.getDeclaringClass(), field);
        _getter = doFindGetter(field.getDeclaringClass(), field);
    }


    public Object getValue(Object target, boolean useGetter) throws Exception {
        if (useGetter) {
            if (_getter != null) {
                if (_getter.isAccessible() == false) {
                    _getter.setAccessible(true);
                }

                return _getter.invoke(target);
            }
        }

        return field.get(target);
    }

    public void setValue(Object target, Object value, boolean useSetter) throws Exception {
        if (useSetter) {
            if (_setter != null) {
                if (_setter.isAccessible() == false) {
                    _setter.setAccessible(true);
                }

                _setter.invoke(target, value);
            }
        }

        field.set(target, value);
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
        if (Asserts.isEmpty(name)) {
            return field.getName();
        } else {
            return name;
        }
    }


    public boolean isFinal() {
        return Modifier.isFinal(field.getModifiers());
    }

    public boolean isAsString() {
        return asString;
    }

    public boolean hasSetter() {
        return _setter != null;
    }

    public boolean hasGetter() {
        return _getter != null;
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

    /// ////////////////

    /**
     * 查找设置器
     */
    private static Method doFindSetter(Class<?> tCls, Field field) {
        String fieldName = field.getName();
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        String setMethodName = "set" + firstLetter + fieldName.substring(1);

        try {
            Method setFun = tCls.getMethod(setMethodName, new Class[]{field.getType()});
            if (setFun != null) {
                return setFun;
            }
        } catch (NoSuchMethodException e) {
            //正常情况，不用管
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 查找设置器
     */
    private static Method doFindGetter(Class<?> tCls, Field field) {
        String fieldName = field.getName();
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        String setMethodName = "get" + firstLetter + fieldName.substring(1);

        try {
            Method getFun = tCls.getMethod(setMethodName);
            if (getFun != null) {
                return getFun;
            }
        } catch (NoSuchMethodException e) {
            //正常情况，不用管
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}