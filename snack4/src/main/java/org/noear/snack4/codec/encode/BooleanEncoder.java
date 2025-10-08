package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class BooleanEncoder implements ObjectEncoder<Boolean> {
    @Override
    public ONode encode(EncodeContext ctx, Boolean value) {
        return new ONode(ctx.getOpts(), value);
    }
}