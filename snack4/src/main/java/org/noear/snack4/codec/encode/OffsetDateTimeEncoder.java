package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.NodeEncoder;

import java.time.OffsetDateTime;
import java.util.Date;

/**
 *
 * @author noear 2025/10/3 created
 */
public class OffsetDateTimeEncoder implements NodeEncoder<OffsetDateTime> {
    @Override
    public ONode encode(Options opts, ONodeAttr attr, OffsetDateTime value) {
        return new ONode(Date.from(value.toInstant()));
    }
}
