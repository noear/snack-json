package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.NodeDecoder;

import java.lang.reflect.Array;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class _ArrayDecoder implements NodeDecoder<Object> {
    private static final _ArrayDecoder instance = new _ArrayDecoder();

    public static _ArrayDecoder instance() {
        return instance;
    }

    @Override
    public Object decode(Options opts, ONodeAttr attr, ONode node, Class<?> clazz) {
        Class<?> itemType = clazz.getComponentType();
        Object array = Array.newInstance(itemType, node.size());

        for (int i = 0; i < node.size(); i++) {
            Array.set(array, i, node.get(i).toBean(itemType));
        }

        return array;
    }
}