package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;

import java.text.SimpleDateFormat;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class SimpleDateFormatEncoder implements ObjectEncoder<SimpleDateFormat> {
    @Override
    public ONode encode(EncodeContext ctx, SimpleDateFormat value) {
        ONode node = new ONode();
        node.set("pattern", value.toPattern());
        return node;
    }
}
