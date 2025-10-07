package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectPatternEncoder;

import java.util.Calendar;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class _CalendarPatternEncoder implements ObjectPatternEncoder<Calendar> {
    @Override
    public boolean canEncode(Object value) {
        return value instanceof Calendar;
    }

    @Override
    public ONode encode(EncodeContext ctx, Calendar value) {
        return new ONode(value.getTime());
    }
}
