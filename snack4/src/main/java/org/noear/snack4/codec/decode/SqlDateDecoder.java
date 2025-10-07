package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;

import java.sql.Date;

/**
 *
 * @author noear 2025/10/6 created
 *
 */
public class SqlDateDecoder implements ObjectDecoder<Date> {
    @Override
    public Date decode(DecodeContext ctx, ONode node) {
        return new Date(node.getLong());
    }
}
