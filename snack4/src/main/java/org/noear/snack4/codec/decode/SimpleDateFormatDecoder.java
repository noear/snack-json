package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.NodeDecoder;
import org.noear.snack4.exception.TypeConvertException;

import java.text.SimpleDateFormat;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class SimpleDateFormatDecoder implements NodeDecoder<SimpleDateFormat> {
    @Override
    public SimpleDateFormat decode(Options opts, ONodeAttr attr, ONode node, Class<?> clazz) {
        if (node.isString()) {
            return new SimpleDateFormat(node.getString());
        } else if (node.isObject()) {
            String pattern = node.get("pattern").getString();
            return new SimpleDateFormat(pattern);
        }

        throw new TypeConvertException("Cannot be converted to SimpleDateFormat: " + node);
    }
}
