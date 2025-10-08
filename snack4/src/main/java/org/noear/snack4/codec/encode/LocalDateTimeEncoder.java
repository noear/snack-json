package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;
import org.noear.snack4.util.Asserts;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author noear 2025/10/3 created
 */
public class LocalDateTimeEncoder implements ObjectEncoder<LocalDateTime> {
    @Override
    public ONode encode(EncodeContext ctx, LocalDateTime value, ONode target) {
        if (ctx.getAttr() != null) {
            if (Asserts.isNotEmpty(ctx.getAttr().format())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ctx.getAttr().format());
                return target.setValue(formatter.format(value));
            }
        }

        Instant instant = value.atZone(Options.DEF_TIME_ZONE.toZoneId()).toInstant();
        return target.setValue(new Date((instant.getEpochSecond() * 1000) + (instant.getNano() / 1000_000)));
    }
}
