package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;

import java.util.UUID;

/**
 *
 * @author noear 2025/10/3 created
 */
public class UUIDDecoder implements ObjectDecoder<UUID> {
    @Override
    public UUID decode(DecodeContext ctx, ONode node) {
        return UUID.fromString(node.getString());
    }
}