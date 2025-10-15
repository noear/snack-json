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
package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectPatternDecoder;
import org.noear.snack4.codec.CodecException;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;

/**
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public class _ArrayPatternDecoder implements ObjectPatternDecoder<Object> {

    @Override
    public boolean canDecode(Class<?> clazz) {
        return clazz.isArray();
    }

    @Override
    public Object decode(DecodeContext ctx, ONode node) {
        Class<?> itemType = null;
        if (ctx.getGenericType() instanceof GenericArrayType) {
            itemType = (Class<?>) ((GenericArrayType) ctx.getGenericType()).getGenericComponentType();
        } else {
            itemType = ctx.getType().getComponentType();
        }

        if (node.isArray()) {
            int size = node.getArray().size();
            Object array = Array.newInstance(itemType, size);

            for (int i = 0; i < size; i++) {
                Array.set(array, i, node.get(i).toBean(itemType));
            }

            return array;
        } else if (node.isString()) {
            String[] strArray = node.toString().split(",");

            Object array = Array.newInstance(itemType, strArray.length);

            for (int i = 0; i < strArray.length; i++) {
                Array.set(array, i, new ONode(ctx.getOptions(), strArray[i]).toBean(itemType));
            }
            return array;
        } else {
            throw new CodecException("Cannot be converted to Array: " + node);
        }
    }
}