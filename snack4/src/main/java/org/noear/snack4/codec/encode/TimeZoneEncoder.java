package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;

import java.util.TimeZone;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class TimeZoneEncoder implements ObjectEncoder<TimeZone> {
    @Override
    public ONode encode(EncodeContext ctx, TimeZone value) {
        return new ONode(value.getID());
    }
}
