package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;

import java.util.concurrent.atomic.LongAdder;

/**
 *
 * @author noear 2025/10/7 created
 *
 */
public class LongAdderEncoder implements ObjectEncoder<LongAdder> {
    @Override
    public ONode encode(EncodeContext ctx, LongAdder value) {
        return new ONode(value.longValue());
    }
}
