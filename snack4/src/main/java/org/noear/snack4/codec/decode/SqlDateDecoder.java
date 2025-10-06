package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectDecoder;

import java.sql.Date;

/**
 *
 * @author noear 2025/10/6 created
 *
 */
public class SqlDateDecoder implements ObjectDecoder<Date> {
    @Override
    public Date decode(Options opts, ONodeAttr attr, ONode node, Class<?> clazz) {
        return new Date(node.getLong());
    }
}
