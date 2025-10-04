package org.noear.snack4.codec.encode;

import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.NodeEncoder;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class StringEncoder implements NodeEncoder<String> {
    @Override
    public ONode encode(Options opts, ONodeAttr attr, String value) {
        if (opts.isFeatureEnabled(Feature.Read_UnwrapJsonString)) {
            if (value.length() > 1) {
                char c1 = value.charAt(0);
                char c2 = value.charAt(value.length() - 1);

                if ((c1 == '{' && c2 == '}') || (c1 == '[' && c2 == ']')) {
                    return ONode.load(value);
                }
            }
        }

        return new ONode(value);
    }
}