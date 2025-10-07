package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;

/**
 *
 * @author noear 2025/10/6 created
 */
public class ClassDecoder implements ObjectDecoder<Class> {
    @Override
    public Class decode(DecodeContext ctx, ONode node) {
        return ctx.getOpts().loadClass(node.toString());
    }
}
