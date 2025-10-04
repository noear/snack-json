package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectEncoder;

import java.time.ZonedDateTime;
import java.util.Date;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class ZonedDateTimeEncoder implements ObjectEncoder<ZonedDateTime> {
    @Override
    public ONode encode(Options opts, ONodeAttr attr, ZonedDateTime value) {
        return new ONode(Date.from(value.toInstant()));
    }
}
