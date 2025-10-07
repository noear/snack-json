package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;

import java.io.File;
import java.net.URI;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class URIDecoder implements ObjectDecoder<URI> {
    @Override
    public URI decode(DecodeContext ctx, ONode node) {
        return URI.create(node.getString());
    }
}
