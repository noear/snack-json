package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.NodeEncoder;

import java.util.TimeZone;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class TimeZoneEncoder implements NodeEncoder<TimeZone> {
    @Override
    public ONode encode(Options opts, ONodeAttr attr, TimeZone value) {
        return new ONode(value.getID());
    }
}
