package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;

/**
 *
 * @author noear 2025/10/6 created
 */
public class StackTraceElementEncoder implements ObjectEncoder<StackTraceElement> {
    @Override
    public ONode encode(EncodeContext ctx, StackTraceElement value, ONode target) {
        target.set("className", value.getClassName());
        target.set("methodName", value.getMethodName());
        target.set("fileName", value.getFileName());
        target.set("lineNumber", value.getLineNumber());

        return target;
    }
}
