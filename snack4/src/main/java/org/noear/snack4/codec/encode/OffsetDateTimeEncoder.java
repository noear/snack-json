package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
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
    public ONode encode(Options opts, ONodeAttr attr, OffsetDateTime value) {
        if (attr != null) {
            if (Asserts.isNotEmpty(attr.format())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(attr.format());
                return new ONode(formatter.format(value));
            }
        }

        return new ONode(Date.from(value.toInstant()));
    }
}
