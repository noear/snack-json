package demo.snack4._models;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;

/**
 * @author noear 2022/4/17 created
 */
public class NodeEncoderImpl implements ObjectEncoder<String> {
    @Override
    public ONode encode(EncodeContext ctx, String value) {
        return null;
    }
}
