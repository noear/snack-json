package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;
import org.noear.snack4.util.Asserts;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author noear 2025/10/3 created
 */
public class OffsetTimeEncoder implements ObjectEncoder<OffsetTime> {
    @Override
    public ONode encode(EncodeContext ctx, OffsetTime value, ONode target) {
        if (ctx.getAttr() != null) {
            if (Asserts.isNotEmpty(ctx.getAttr().format())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ctx.getAttr().format());
                return target.setValue(formatter.format(value));
            }
        }

        Instant instant = value.atDate(LocalDate.of(1970, 1, 1)).toInstant();
        return target.setValue(Date.from(instant));
    }
}
