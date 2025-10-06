package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.codec.util.DateUtil;
import org.noear.snack4.util.Asserts;

import java.time.OffsetTime;
import java.time.ZoneId;

/**
 *
 * @author noear 2025/10/6 created
 */
public class OffsetTimeDecoder implements ObjectDecoder<OffsetTime> {
    @Override
    public OffsetTime decode(Options opts, ONodeAttr attr, ONode node, Class<?> clazz) {
        ZoneId zoneId = opts.getTimeZone().toZoneId();
        if (attr != null && Asserts.isNotEmpty(attr.timezone())) {
            zoneId = ZoneId.of(attr.timezone());
        }

        return DateUtil.decode(opts, attr, node, clazz).atZone(zoneId).toOffsetDateTime().toOffsetTime();
    }
}