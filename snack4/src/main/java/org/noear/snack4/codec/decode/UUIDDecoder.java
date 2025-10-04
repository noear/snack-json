package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.NodeDecoder;

import java.util.UUID;

/**
 *
 * @author noear 2025/10/3 created
 */
public class UUIDDecoder implements NodeDecoder<UUID> {
    @Override
    public UUID decode(Options opts, ONodeAttr attr, ONode node, Class<?> clazz) {
        return UUID.fromString(node.getString());
    }
}