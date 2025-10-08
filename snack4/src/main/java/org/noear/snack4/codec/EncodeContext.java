package org.noear.snack4.codec;

import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;

/**
 *
 * @author noear 2025/10/7 created
 *
 */
public class EncodeContext {
    private final Options options;
    private final ONodeAttr attr;

    public EncodeContext(Options options, ONodeAttr attr) {
        this.options = options;
        this.attr = attr;
    }

    public Options getOptions() {
        return options;
    }

    public ONodeAttr getAttr() {
        return attr;
    }
}
