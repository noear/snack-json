package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.exception.TypeConvertException;

import java.io.File;
import java.net.URI;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class URIDecoder implements ObjectDecoder<URI> {
    @Override
    public URI decode(Options opts, ONodeAttr attr, ONode node, Class<?> clazz) {
        return URI.create(node.getString());
    }
}
