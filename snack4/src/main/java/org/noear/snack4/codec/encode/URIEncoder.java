package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectEncoder;

import java.net.URI;

/**
 *
 * @author noear 2025/10/6 created
 *
 */
public class URIEncoder implements ObjectEncoder<URI> {
    @Override
    public ONode encode(Options opts, ONodeAttr attr, URI value) {
        return new ONode(value.toString());
    }
}
