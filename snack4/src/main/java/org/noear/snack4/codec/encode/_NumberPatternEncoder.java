package org.noear.snack4.codec.encode;

import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectPatternEncoder;
import org.noear.snack4.util.Asserts;

/**
 *
 * @author noear 2025/10/3 created
 */
public class _NumberPatternEncoder implements ObjectPatternEncoder<Number> {
    @Override
    public boolean canEncode(Object value) {
        return value instanceof Number;
    }

    @Override
    public ONode encode(EncodeContext ctx, Number value, ONode target) {
        if (ctx.getAttr() != null) {
            if (ctx.getAttr().hasSerializeFeature(Feature.Write_NumbersAsString)) {
                return target.setValue(value.toString());
            }

            if (ctx.getAttr().hasSerializeFeature(Feature.Write_BigNumbersAsString)) {
                if (Asserts.isBigNumber(value)) {
                    return target.setValue(value.toString());
                }
            }
        }

        return target.setValue(value);
    }
}