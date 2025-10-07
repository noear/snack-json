package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;

/**
 *
 * @author noear 2025/10/3 created
 */
public class IntegerDecoder implements ObjectDecoder<Integer> {
    @Override
    public Integer decode(DecodeContext ctx, ONode node) {
        return node.getInt(ctx.getType().isPrimitive() ? 0 : null);
    }
}