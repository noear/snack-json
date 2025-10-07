package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.codec.util.DateUtil;

import java.util.Date;

/**
 *
 * @author noear 2025/10/3 created
 */
public class DateDecoder implements ObjectDecoder<Date> {
    @Override
    public Date decode(DecodeContext ctx, ONode node) {
        if(node.isNullOrEmpty()){
            return null;
        }

        return Date.from(DateUtil.decode(ctx, node));
    }
}