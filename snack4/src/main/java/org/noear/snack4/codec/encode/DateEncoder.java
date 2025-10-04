package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectEncoder;
import org.noear.snack4.codec.util.DateUtil;
import org.noear.snack4.util.Asserts;

import java.util.Date;

/**
 *
 * @author noear 2025/10/3 created
 */
public class DateEncoder implements ObjectEncoder<Date> {
    @Override
    public ONode encode(Options opts, ONodeAttr attr, Date value) {
        if (attr != null) {
            if (Asserts.isNotEmpty(attr.format())) {
                return new ONode(DateUtil.format(value, attr.format()));
            }
        }

        return new ONode(value);
    }
}
