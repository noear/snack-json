package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;

import java.util.TimeZone;

/**
 *
 * @author noear 2025/10/6 created
 *
 */
public class TimeZoneDecoder implements ObjectDecoder<TimeZone> {
    @Override
    public TimeZone decode(DecodeContext ctx, ONode node) {
        return TimeZone.getTimeZone(node.getString());
    }
}
