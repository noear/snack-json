package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.NodeDecoder;
import org.noear.snack4.exception.TypeConvertException;

import java.io.File;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class FileDecoder implements NodeDecoder<File> {
    @Override
    public File decode(Options opts, ONodeAttr attr, ONode node, Class<?> clazz) {
        if (node.isString()) {
            return new File(node.getString());
        }

        throw new TypeConvertException("Cannot be converted to File: " + node);
    }
}
