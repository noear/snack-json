package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectDecoder;

/**
 *
 * @author noear 2025/10/6 created
 */
public class StackTraceElementDecoder implements ObjectDecoder<StackTraceElement> {
    @Override
    public StackTraceElement decode(Options opts, ONodeAttr attr, ONode node, Class<?> clazz) {
        return new StackTraceElement(
                node.get("className").getString(),
                node.get("methodName").getString(),
                node.get("fileName").getString(),
                node.get("lineNumber").getInt());
    }
}
