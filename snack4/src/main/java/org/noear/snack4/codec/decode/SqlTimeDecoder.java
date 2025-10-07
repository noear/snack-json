package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;

import java.sql.Time;

/**
 *
 * @author noear 2025/10/6 created
 *
 */
public class SqlTimeDecoder implements ObjectDecoder<Time> {
    @Override
    public Time decode(DecodeContext ctx, ONode node) {
        return new Time(node.getLong());
    }
}
