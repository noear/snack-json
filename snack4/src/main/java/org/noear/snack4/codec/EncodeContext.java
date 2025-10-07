package org.noear.snack4.codec;

import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;

/**
 *
 * @author noear 2025/10/7 created
 *
 */
public class EncodeContext {
    private final Options opts;
    private final ONodeAttr attr;

    public EncodeContext(Options opts, ONodeAttr attr) {
        this.opts = opts;
        this.attr = attr;
    }

    public Options getOpts() {
        return opts;
    }

    public ONodeAttr getAttr() {
        return attr;
    }
}
