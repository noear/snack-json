package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectPatternDecoder;

/**
 *
 * @author noear 2025/10/7 created
 *
 */
public class _ThrowablePatternDecoder implements ObjectPatternDecoder<Throwable> {
    @Override
    public boolean canDecode(Class<?> clazz) {
        return Throwable.class.isAssignableFrom(clazz);
    }

    @Override
    public Throwable decode(Options opts, ONodeAttr attr, ONode node, Class<?> clazz) {
        String message = node.get("message").getString();

        try {
            if (message == null) {
                return (Throwable) clazz.getDeclaredConstructor().newInstance();
            } else {
                return (Throwable) clazz.getDeclaredConstructor(String.class).newInstance(message);
            }
        } catch (Exception ex) {
            return null;
        }
    }
}
