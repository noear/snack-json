package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.NodeEncoder;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class ClassEncoder implements NodeEncoder<Class> {
    @Override
    public ONode encode(Options opts, ONodeAttr attr, Class value) {
        return new ONode(value.getName());
    }
}
