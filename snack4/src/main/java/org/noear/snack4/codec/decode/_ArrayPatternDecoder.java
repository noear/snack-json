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
 *
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
            Object array = Array.newInstance(itemType, node.size());

            for (int i = 0; i < node.size(); i++) {
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