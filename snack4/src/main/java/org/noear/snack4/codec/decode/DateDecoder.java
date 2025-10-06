package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.codec.util.DateUtil;

import java.util.Date;

/**
 *
 * @author noear 2025/10/3 created
 */
public class DateDecoder implements ObjectDecoder<Date> {
    @Override
    public Date decode(Options opts, ONodeAttr attr, ONode node, Class<?> clazz) {
        return Date.from(DateUtil.decode(opts, attr, node, clazz));
    }
}