package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectDecoder;

/**
 *
 * @author noear 2025/10/3 created
 */
public class LongDecoder implements ObjectDecoder<Long> {
    @Override
    public Long decode(Options opts, ONodeAttr attr, ONode node, Class<?> clazz) {
        return node.getLong(clazz.isPrimitive() ? 0L : null);
    }
}