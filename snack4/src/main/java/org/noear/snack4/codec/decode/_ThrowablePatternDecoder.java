package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
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
    public Throwable decode(DecodeContext ctx, ONode node) {
        String message = node.get("message").getString();

        try {
            if (message == null) {
                return (Throwable) ctx.getType().getDeclaredConstructor().newInstance();
            } else {
                return (Throwable) ctx.getType().getDeclaredConstructor(String.class).newInstance(message);
            }
        } catch (Exception ex) {
            return null;
        }
    }
}
