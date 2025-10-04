package org.noear.snack4.codec.encode;

import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.DEFAULTS;
import org.noear.snack4.codec.NodeEncoder;

import java.text.SimpleDateFormat;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class SimpleDateFormatEncoder implements NodeEncoder<SimpleDateFormat> {
    @Override
    public ONode encode(Options opts, ONodeAttr attr, SimpleDateFormat value) {
        ONode node = new ONode();
        node.set("pattern", value.toPattern());
        return node;
    }
}
