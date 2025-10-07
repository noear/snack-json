package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;

import java.math.BigInteger;

/**
 *
 * @author noear 2025/10/6 created
 *
 */
public class BigIntegerDecoder implements ObjectDecoder<BigInteger> {
    @Override
    public BigInteger decode(DecodeContext ctx, ONode node) {
        if (node.isNumber()) {
            if (node.getValue() instanceof BigInteger) {
                return (BigInteger) node.getValue();
            }
        }

        return new BigInteger(node.getString());
    }
}
