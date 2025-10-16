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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author noear 2025/2/26 created
 * @since 4.0
 */
public class GenericArrayTypeImpl implements GenericArrayType {
    private final Type genericComponentType;

    public GenericArrayTypeImpl(Type componentType) {
        this.genericComponentType = componentType;
    }

    @Override
    public Type getGenericComponentType() {
        return this.genericComponentType;
    }

    @Override
    public String toString() {
        Type tt = this.getGenericComponentType();
        StringBuilder sb = new StringBuilder();
        if (tt instanceof Class) {
            sb.append(((Class) tt).getName());
        } else {
            sb.append(tt);
        }

        sb.append("[]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object var1) {
        if (var1 instanceof GenericArrayType) {
            GenericArrayType var2 = (GenericArrayType) var1;
            return Objects.equals(this.genericComponentType, var2.getGenericComponentType());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.genericComponentType);
    }
}