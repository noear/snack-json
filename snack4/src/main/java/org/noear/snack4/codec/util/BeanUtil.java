package org.noear.snack4.codec.util;

import org.noear.snack4.exception.SnackException;
import org.noear.snack4.util.Asserts;

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.sql.Clob;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class BeanUtil {
    /**
     * 将 Clob 转为 String
     */
    public static String clobToString(Clob clob) {

        Reader reader = null;
        StringBuilder buf = new StringBuilder();

        try {
            reader = clob.getCharacterStream();

            char[] chars = new char[2048];
            for (; ; ) {
                int len = reader.read(chars, 0, chars.length);
                if (len < 0) {
                    break;
                }
                buf.append(chars, 0, len);
            }
        } catch (Throwable e) {
            throw new SnackException("Read string from reader error", e);
        }

        String text = buf.toString();

        if (reader != null) {
            try {
                reader.close();
            } catch (Throwable e) {
                throw new SnackException("Read string from reader error", e);
            }
        }

        return text;
    }

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