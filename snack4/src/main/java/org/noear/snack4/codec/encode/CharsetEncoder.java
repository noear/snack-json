package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;

import java.nio.charset.Charset;

/**
 *
 * @author noear 2025/10/7 created
 *
 */
public class CharsetEncoder implements ObjectEncoder<Charset> {
    @Override
    public ONode encode(EncodeContext ctx, Charset value) {
        return new ONode(value.name());
    }
}
