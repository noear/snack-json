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
import org.noear.snack4.annotation.ONodeAttrHolder;
import org.noear.snack4.core.util.Asserts;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author noear 2025/3/16 created
 * @since 4.0
 */
public class FieldWrap implements Property {
    private final Field field;
    private final TypeWrap fieldTypeWrap;

    private ONodeAttrHolder attr;
    private String name;

    private boolean isFinal;
    private boolean isTransient;

    public FieldWrap(TypeWrap owner, Field field) {
        if (field.isAccessible() == false) {
            field.setAccessible(true);
        }

        this.field = field;
        this.fieldTypeWrap = TypeWrap.from(GenericUtil.reviewType(field.getGenericType(), getGenericInfo(owner, field)));

        this.isFinal = Modifier.isFinal(field.getModifiers());
        this.isTransient = Modifier.isTransient(field.getModifiers());

        ONodeAttr attrAnno = field.getAnnotation(ONodeAttr.class);
        if (attrAnno != null) {
            this.name = attrAnno.name();
            this.attr = new ONodeAttrHolder(attrAnno, isTransient);
        } else {
            this.attr = new ONodeAttrHolder(null, isTransient);
        }

        if (Asserts.isEmpty(name)) {
            name = field.getName();
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

    public Field getField() {
        return field;
    }

    @Override
    public Object getValue(Object target) throws Exception {
        return field.get(target);
    }

    @Override
    public void setValue(Object target, Object value) throws Exception {
        if (isFinal == false) {
            field.set(target, value);
        }
    }

    @Override
    public TypeWrap getTypeWrap() {
        return fieldTypeWrap;
    }

    @Override
    public ONodeAttrHolder getAttr() {
        return attr;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return field.toString();
    }
}