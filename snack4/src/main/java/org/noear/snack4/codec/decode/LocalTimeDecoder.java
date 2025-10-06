package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.codec.util.DateUtil;

import java.time.LocalTime;

/**
 *
 * @author noear 2025/10/6 created
 */
public class LocalTimeDecoder implements ObjectDecoder<LocalTime> {

    @Override
    public LocalTime decode(Options opts, ONodeAttr attr, ONode node, Class<?> clazz) {
        return LocalTime.from(DateUtil.decode(opts, attr, node, clazz));
    }
}
