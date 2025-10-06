package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.exception.TypeConvertException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class URLDecoder implements ObjectDecoder<URL> {
    @Override
    public URL decode(Options opts, ONodeAttr attr, ONode node, Class<?> clazz) {
        try {
            return URI.create(node.getString()).toURL();
        } catch (MalformedURLException e) {
            throw new TypeConvertException(e);
        }
    }
}