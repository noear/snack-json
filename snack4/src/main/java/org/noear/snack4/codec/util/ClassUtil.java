package org.noear.snack4.codec.util;

import org.noear.snack4.exception.SnackException;

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.sql.Clob;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class ClassUtil {
    /**
     * 新建实例
     */
    public static <T> T newInstance(Class<T> clz) throws SnackException {
        try {
            if (clz.isInterface()) {
                return null;
            } else {
                Constructor<T> constructor = clz.getDeclaredConstructor();
                if (constructor.isAccessible() == false) {
                    constructor.setAccessible(true);
                }

                return constructor.newInstance();
            }
        } catch (Throwable e) {
            throw new SnackException("Instantiation failed: " + clz.getName(), e);
        }
    }
}