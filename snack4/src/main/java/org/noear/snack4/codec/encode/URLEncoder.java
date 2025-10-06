package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectEncoder;

import java.net.URI;
import java.net.URL;

/**
 *
 * @author noear 2025/10/6 created
 *
 */
public class URLEncoder implements ObjectEncoder<URL> {
    @Override
    public ONode encode(Options opts, ONodeAttr attr, URL value) {
        return new ONode(value.toString());
    }
}
