package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectEncoder;
import org.noear.snack4.util.Asserts;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author noear 2025/10/3 created
 */
public class LocalDateEncoder implements ObjectEncoder<LocalDate> {
    @Override
    public ONode encode(Options opts, ONodeAttr attr, LocalDate value) {
        if (attr != null) {
            if (Asserts.isNotEmpty(attr.format())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(attr.format());
                if(Asserts.isNotEmpty(attr.timezone())){
                    formatter.withZone(ZoneId.of(attr.timezone()));
                }

                return new ONode(formatter.format(value));
            }
        }

        Instant instant = value.atTime(LocalTime.MIN).atZone(Options.DEF_TIME_ZONE.toZoneId()).toInstant();
        return new ONode(new Date(instant.getEpochSecond() * 1000));
    }
}
