package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.codec.util.DateUtil;

import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 *
 * @author noear 2025/10/6 created
 */
public class OffsetDateTimeDecoder implements ObjectDecoder<OffsetDateTime> {
    @Override
    public OffsetDateTime decode(DecodeContext ctx, ONode node) {
        if(node.isNullOrEmpty()){
            return null;
        }

        ZoneId zoneId = DateUtil.zoneIdOf(ctx);

        return DateUtil.decode(ctx, node).atZone(zoneId).toOffsetDateTime();
    }
}
