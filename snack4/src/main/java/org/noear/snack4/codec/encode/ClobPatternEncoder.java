package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.NodePatternEncoder;
import org.noear.snack4.codec.util.BeanUtil;

import java.sql.Clob;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class ClobPatternEncoder implements NodePatternEncoder<Clob> {
    @Override
    public boolean canEncode(Class clazz) {
        return Clob.class.isAssignableFrom(clazz);
    }

    @Override
    public ONode encode(Options opts, ONodeAttr attr, Clob value) {
        return new ONode(BeanUtil.clobToString(value));
    }
}
