package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.NodePatternEncoder;

import java.util.Calendar;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class _CalendarPatternEncoder implements NodePatternEncoder<Calendar> {
    @Override
    public boolean canEncode(Object value) {
        return value instanceof Calendar;
    }

    @Override
    public ONode encode(Options opts, ONodeAttr attr, Calendar value) {
        return new ONode(value.getTime());
    }
}
