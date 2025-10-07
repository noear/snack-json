package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.codec.util.DateUtil;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 *
 * @author noear 2025/10/6 created
 *
 */
public class ZonedDateTimeDecoder implements ObjectDecoder<ZonedDateTime> {
    @Override
    public ZonedDateTime decode(DecodeContext ctx, ONode node) {
        ZoneId zoneId = DateUtil.zoneIdOf(ctx);

        return DateUtil.decode(ctx, node).atZone(zoneId);
    }
}
