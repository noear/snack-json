package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;

import java.time.Duration;

/**
 *
 * @author noear 2025/10/7 created
 *
 */
public class DurationDecoder implements ObjectDecoder<Duration> {
    @Override
    public Duration decode(DecodeContext ctx, ONode node) {
        if (node.isNullOrEmpty()) {
            return null;
        } else {
            String tmp = node.getString().toUpperCase();
            if (tmp.indexOf('P') != 0) {
                if (tmp.indexOf('D') > 0) {
                    tmp = "P" + tmp;
                } else {
                    tmp = "PT" + tmp;
                }
            }

            return Duration.parse(tmp);
        }
    }
}
