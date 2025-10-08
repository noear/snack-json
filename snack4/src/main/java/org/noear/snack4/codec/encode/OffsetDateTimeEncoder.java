package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;
import org.noear.snack4.util.Asserts;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author noear 2025/10/3 created
 */
public class OffsetDateTimeEncoder implements ObjectEncoder<OffsetDateTime> {
    @Override
    public ONode encode(EncodeContext ctx, OffsetDateTime value, ONode target) {
        if (ctx.getAttr() != null) {
            if (Asserts.isNotEmpty(ctx.getAttr().getFormat())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ctx.getAttr().getFormat());
                return target.setValue(formatter.format(value));
            }
        }

        return target.setValue(Date.from(value.toInstant()));
    }
}
