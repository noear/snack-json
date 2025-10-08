package org.noear.snack4.codec.factory;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.codec.ObjectPatternFactory;

/**
 *
 * @author noear 2025/10/8 created
 */
public class _ThrowablePatternFactory implements ObjectPatternFactory<Throwable> {
    @Override
    public boolean calCreate(Class<?> clazz) {
        return Throwable.class.isAssignableFrom(clazz);
    }

    @Override
    public Throwable create(Options opts, ONode node, Class<?> clazz) {
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