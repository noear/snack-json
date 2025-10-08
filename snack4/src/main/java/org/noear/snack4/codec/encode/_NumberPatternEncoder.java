package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectPatternEncoder;

/**
 *
 * @author noear 2025/10/3 created
 */
public class _NumberPatternEncoder implements ObjectPatternEncoder<Number> {
    @Override
    public boolean canEncode(Object value) {
        return value instanceof Number;
    }

    @Override
    public ONode encode(EncodeContext ctx, Number value) {
        return new ONode(ctx.getOpts(), value);
    }
}