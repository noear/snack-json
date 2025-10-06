package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
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
    public ONode encode(Options opts, ONodeAttr attr, Date value) {
        if (attr != null) {
            if (Asserts.isNotEmpty(attr.format())) {
                if (Asserts.isNotEmpty(attr.timezone())) {
                    return new ONode(DateUtil.format(value, attr.format(), TimeZone.getTimeZone(ZoneId.of(attr.timezone()))));
                } else {
                    return new ONode(DateUtil.format(value, attr.format()));
                }
            }
        }

        return new ONode(value);
    }
}