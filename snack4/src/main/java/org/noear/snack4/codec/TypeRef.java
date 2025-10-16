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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 类型引用
 *
 * @author noear 2019/11/22 created
 * @since 4.0
 * */
public abstract class TypeRef<T> {
    protected final Type type;

    protected TypeRef() {
        Type sc = this.getClass().getGenericSuperclass();
        this.type = ((ParameterizedType) sc).getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }
}
