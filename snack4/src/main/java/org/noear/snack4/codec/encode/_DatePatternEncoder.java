package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectPatternEncoder;
import org.noear.snack4.codec.util.DateUtil;
import org.noear.snack4.util.Asserts;

import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author noear 2025/10/3 created
 */
public class _DatePatternEncoder implements ObjectPatternEncoder<Date> {
    @Override
    public boolean canEncode(Object value) {
        return value instanceof Date;
    }

    @Override
    public ONode encode(EncodeContext ctx, Date value) {
        if (ctx.getAttr() != null) {
            if (Asserts.isNotEmpty(ctx.getAttr().format())) {
                if (Asserts.isNotEmpty(ctx.getAttr().timezone())) {
                    return new ONode(ctx.getOpts(), DateUtil.format(value, ctx.getAttr().format(), TimeZone.getTimeZone(ZoneId.of(ctx.getAttr().timezone()))));
                } else {
                    return new ONode(ctx.getOpts(), DateUtil.format(value, ctx.getAttr().format()));
                }
            }
        }

        return new ONode(ctx.getOpts(), value);
    }
}