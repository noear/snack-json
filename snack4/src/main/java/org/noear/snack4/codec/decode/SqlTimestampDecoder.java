package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;

import java.sql.Timestamp;

/**
 *
 * @author noear 2025/10/6 created
 *
 */
public class SqlTimestampDecoder implements ObjectDecoder<Timestamp> {
    @Override
    public Timestamp decode(DecodeContext ctx, ONode node) {
        return new Timestamp(node.getLong());
    }
}
