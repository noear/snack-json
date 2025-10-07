package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class StringDecoder implements ObjectDecoder<String> {
    @Override
    public String decode(DecodeContext ctx, ONode node) {
        return node.getString();
    }
}
