package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;

import java.util.UUID;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class UUIDEncoder implements ObjectEncoder<UUID> {
    @Override
    public ONode encode(EncodeContext ctx, UUID value, ONode target) {
        return target.setValue(value.toString());
    }
}