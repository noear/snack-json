package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;

import java.math.BigDecimal;

/**
 *
 * @author noear 2025/10/6 created
 *
 */
public class BigDecimalDecoder implements ObjectDecoder<BigDecimal> {
    @Override
    public BigDecimal decode(DecodeContext ctx, ONode node) {
        if (node.isNumber()) {
            if (node.getValue() instanceof BigDecimal) {
                return (BigDecimal) node.getValue();
            }
        }

        return new BigDecimal(node.getString());
    }
}
