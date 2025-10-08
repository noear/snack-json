package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;
import org.noear.snack4.util.Asserts;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class ZonedDateTimeEncoder implements ObjectEncoder<ZonedDateTime> {
    @Override
    public ONode encode(EncodeContext ctx, ZonedDateTime value) {
        if (ctx.getAttr() != null) {
            if (Asserts.isNotEmpty(ctx.getAttr().format())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ctx.getAttr().format());
                return new ONode(ctx.getOpts(), formatter.format(value));
            }
        }
        
        return new ONode(ctx.getOpts(), Date.from(value.toInstant()));
    }
}
