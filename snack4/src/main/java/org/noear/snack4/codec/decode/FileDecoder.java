package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.exception.TypeConvertException;

import java.io.File;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class FileDecoder implements ObjectDecoder<File> {
    @Override
    public File decode(DecodeContext ctx, ONode node) {
        if (node.isString()) {
            return new File(node.getString());
        }

        throw new TypeConvertException("Cannot be converted to File: " + node);
    }
}
