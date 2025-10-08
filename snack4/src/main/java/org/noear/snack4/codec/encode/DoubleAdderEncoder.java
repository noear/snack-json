package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;

import java.util.concurrent.atomic.DoubleAdder;

/**
 *
 * @author noear 2025/10/7 created
 *
 */
public class DoubleAdderEncoder implements ObjectEncoder<DoubleAdder> {
    @Override
    public ONode encode(EncodeContext ctx, DoubleAdder value) {
        return new ONode(ctx.getOpts(), value.doubleValue());
    }
}
