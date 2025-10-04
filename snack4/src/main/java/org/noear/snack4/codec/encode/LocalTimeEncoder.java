package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.DEFAULTS;
import org.noear.snack4.codec.NodeEncoder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 *
 * @author noear 2025/10/3 created
 */
public class LocalTimeEncoder implements NodeEncoder<LocalTime> {
    @Override
    public ONode encode(Options opts, ONodeAttr attr, LocalTime value) {
        Instant instant = value.atDate(LocalDate.of(1970, 1, 1)).atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toInstant();
        return new ONode(new Date(instant.getEpochSecond() * 1000));
    }
}
