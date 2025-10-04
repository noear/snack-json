package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.NodeEncoder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.Date;

/**
 *
 * @author noear 2025/10/3 created
 */
public class OffsetTimeEncoder implements NodeEncoder<OffsetTime> {
    @Override
    public ONode encode(Options opts, ONodeAttr attr, OffsetTime value) {
        Instant instant = value.atDate(LocalDate.of(1970, 1, 1)).toInstant();
        return new ONode(Date.from(instant));
    }
}
