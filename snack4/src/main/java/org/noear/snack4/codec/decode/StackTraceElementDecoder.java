package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;

/**
 *
 * @author noear 2025/10/6 created
 */
public class StackTraceElementDecoder implements ObjectDecoder<StackTraceElement> {
    @Override
    public StackTraceElement decode(DecodeContext ctx, ONode node) {
        return new StackTraceElement(
                node.get("className").getString(),
                node.get("methodName").getString(),
                node.get("fileName").getString(),
                node.get("lineNumber").getInt());
    }
}
