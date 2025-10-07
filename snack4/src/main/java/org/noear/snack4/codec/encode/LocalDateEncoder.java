package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.codec.EncodeContext;
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
    public ONode encode(EncodeContext ctx, LocalDate value) {
        if (ctx.getAttr() != null) {
            if (Asserts.isNotEmpty(ctx.getAttr().format())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ctx.getAttr().format());
                if(Asserts.isNotEmpty(ctx.getAttr().timezone())){
                    formatter.withZone(ZoneId.of(ctx.getAttr().timezone()));
                }

                return new ONode(formatter.format(value));
            }
        }

        Instant instant = value.atTime(LocalTime.MIN).atZone(Options.DEF_TIME_ZONE.toZoneId()).toInstant();
        return new ONode(new Date(instant.getEpochSecond() * 1000));
    }
}
