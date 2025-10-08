package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;
import org.noear.snack4.util.Asserts;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author noear 2025/10/3 created
 */
public class LocalTimeEncoder implements ObjectEncoder<LocalTime> {
    @Override
    public ONode encode(EncodeContext ctx, LocalTime value, ONode target) {
        if (ctx.getAttr() != null) {
            if (Asserts.isNotEmpty(ctx.getAttr().getFormat())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ctx.getAttr().getFormat());
                return target.setValue(formatter.format(value));
            }
        }

        Instant instant = value.atDate(LocalDate.of(1970, 1, 1))
                .atZone(Options.DEF_TIME_ZONE.toZoneId())
                .toInstant();

        return target.setValue(new Date(instant.getEpochSecond() * 1000));
    }
}
