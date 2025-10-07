package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;

import java.util.concurrent.atomic.DoubleAdder;

/**
 *
 * @author noear 2025/10/7 created
 *
 */
public class DoubleAdderDecoder implements ObjectDecoder<DoubleAdder> {
    @Override
    public DoubleAdder decode(DecodeContext<DoubleAdder> ctx, ONode node) {
        DoubleAdder tmp = new DoubleAdder();
        tmp.add(node.getDouble());
        return tmp;
    }
}
