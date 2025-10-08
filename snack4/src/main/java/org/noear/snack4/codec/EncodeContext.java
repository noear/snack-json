package org.noear.snack4.codec;

import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.annotation.ONodeAttrHolder;

/**
 *
 * @author noear 2025/10/7 created
 *
 */
public class EncodeContext {
    private final Options options;
    private final ONodeAttrHolder attr;

    public EncodeContext(Options options, ONodeAttrHolder attr) {
        this.options = options;
        this.attr = attr;
    }

    public Options getOptions() {
        return options;
    }

    public ONodeAttrHolder getAttr() {
        return attr;
    }
}
