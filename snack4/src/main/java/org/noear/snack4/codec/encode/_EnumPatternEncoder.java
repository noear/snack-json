package org.noear.snack4.codec.encode;

import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectPatternEncoder;
import org.noear.snack4.codec.util.EnumWrap;
import org.noear.snack4.codec.util.TypeUtil;

/**
 *
 * @author noear 2025/10/3 created
 */
public class _EnumPatternEncoder implements ObjectPatternEncoder<Enum> {

    @Override
    public boolean canEncode(Object value) {
        return value instanceof Enum;
    }

    @Override
    public ONode encode(EncodeContext ctx, Enum value, ONode target) {
        EnumWrap ew = TypeUtil.createEnum(value.getClass());
        Object o = ew.getCustomValue(value);

        //如果为空代表该枚举没有被标注继续采用常规序列化方式
        if (o != null) {
            return target.setValue(o);
        } else {

            if (ctx.getOptions().hasFeature(Feature.Write_EnumUsingName)) {
                return target.setValue(value.name());
            } else {
                return target.setValue(value.ordinal());
            }
        }
    }
}