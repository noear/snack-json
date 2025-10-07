package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class ONodeEncoder implements ObjectEncoder<ONode> {
    @Override
    public ONode encode(EncodeContext ctx, ONode value) {
        return value;
    }
}