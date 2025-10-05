package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectEncoder;

/**
 *
 * @author noear 2025/10/6 created
 */
public class StackTraceElementEncoder implements ObjectEncoder<StackTraceElement> {
    @Override
    public ONode encode(Options opts, ONodeAttr attr, StackTraceElement value) {
        ONode tmp = new ONode();

        tmp.set("className", value.getClassName());
        tmp.set("methodName", value.getMethodName());
        tmp.set("fileName", value.getFileName());
        tmp.set("lineNumber", value.getLineNumber());

        return tmp;
    }
}
