package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectDecoder;

/**
 *
 * @author noear 2025/10/6 created
 */
public class ClassDecoder implements ObjectDecoder<Class> {
    @Override
    public Class decode(Options opts, ONodeAttr attr, ONode node, Class<?> clazz) {
        return opts.loadClass(node.toString());
    }
}
