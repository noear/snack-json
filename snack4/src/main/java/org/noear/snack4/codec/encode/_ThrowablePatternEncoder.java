package org.noear.snack4.codec.encode;

import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectPatternEncoder;

/**
 *
 * @author noear 2025/10/7 created
 *
 */
public class _ThrowablePatternEncoder implements ObjectPatternEncoder<Throwable> {
    @Override
    public boolean canEncode(Object value) {
        return value instanceof Throwable;
    }

    @Override
    public ONode encode(EncodeContext ctx, Throwable value) {
        ONode node = new ONode();
        if (ctx.getOpts().hasFeature(Feature.Write_ClassName)) {
            node.set(ctx.getOpts().getTypePropertyName(), value.getClass().getName());
        }

        return node.set("message", value.getMessage());
    }
}
