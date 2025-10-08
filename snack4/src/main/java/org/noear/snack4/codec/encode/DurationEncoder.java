package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;

import java.time.Duration;

/**
 *
 * @author noear 2025/10/7 created
 *
 */
public class DurationEncoder implements ObjectEncoder<Duration> {
    @Override
    public ONode encode(EncodeContext ctx, Duration value) {
        return new ONode(ctx.getOpts(), value.toString());
    }
}
