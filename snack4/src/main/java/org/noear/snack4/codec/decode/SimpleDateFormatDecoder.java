package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.codec.CodecException;

import java.text.SimpleDateFormat;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class SimpleDateFormatDecoder implements ObjectDecoder<SimpleDateFormat> {
    @Override
    public SimpleDateFormat decode(DecodeContext ctx, ONode node) {
        if (node.isString()) {
            return new SimpleDateFormat(node.getString());
        } else if (node.isObject()) {
            String pattern = node.get("pattern").getString();
            return new SimpleDateFormat(pattern);
        }

        throw new CodecException("Cannot be converted to SimpleDateFormat: " + node);
    }
}
