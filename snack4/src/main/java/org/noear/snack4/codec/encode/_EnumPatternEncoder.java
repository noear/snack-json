package org.noear.snack4.codec.encode;

import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.NodePatternEncoder;
import org.noear.snack4.codec.util.EnumWrapper;
import org.noear.snack4.codec.util.TypeUtil;

/**
 *
 * @author noear 2025/10/3 created
 */
public class _EnumPatternEncoder implements NodePatternEncoder<Enum> {

    @Override
    public boolean canEncode(Object value) {
        return value instanceof Enum;
    }

    @Override
    public ONode encode(Options opts, ONodeAttr attr, Enum value) {
        EnumWrapper ew = TypeUtil.createEnum(value.getClass());
        Object o = ew.getCustomValue(value);

        //如果为空代表该枚举没有被标注继续采用常规序列化方式
        if (o != null) {
            return new ONode(o);
        } else {

            if (opts.isFeatureEnabled(Feature.Write_EnumUsingName)) {
                return new ONode(value.name());
            } else {
                return new ONode(value.ordinal());
            }
        }
    }
}