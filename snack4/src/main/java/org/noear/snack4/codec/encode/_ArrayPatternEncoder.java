package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.NodePatternEncoder;
import org.noear.snack4.codec.ObjectEncoder;

import java.lang.reflect.Array;

/**
 *
 * @author noear 2025/10/4 created
 *
 */
public class _ArrayPatternEncoder implements NodePatternEncoder<Object> {
    @Override
    public boolean canEncode(Object value) {
        return value.getClass().isArray();
    }

    @Override
    public ONode encode(Options opts, ONodeAttr attr, Object value) {
        ONode node = new ONode();

        int len = Array.getLength(value);
        for (int i = 0; i < len; i++) {
            Object item = Array.get(value, i);
            node.add(ObjectEncoder.serialize(item));
        }

        return node;
    }
}
