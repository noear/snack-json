package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectEncoder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

/**
 *
 * @author noear 2025/10/3 created
 */
public class LocalTimeEncoder implements ObjectEncoder<LocalTime> {
    @Override
    public ONode encode(Options opts, ONodeAttr attr, LocalTime value) {
        Instant instant = value.atDate(LocalDate.of(1970, 1, 1))
                .atZone(Options.DEF_TIME_ZONE.toZoneId())
                .toInstant();

        return new ONode(new Date(instant.getEpochSecond() * 1000));
    }
}
