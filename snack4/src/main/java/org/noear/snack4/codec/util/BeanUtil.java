package org.noear.snack4.codec.util;

import org.noear.snack4.exception.ReflectionException;
import org.noear.snack4.exception.TypeConvertException;
import org.noear.snack4.util.Asserts;

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.sql.Clob;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class BeanUtil {
    private static final Map<String, Class<?>> clzCached = new ConcurrentHashMap<>();

    /**
     * @deprecated 3.2.55
     */
    @Deprecated
    public static Class<?> loadClass(String clzName) {
        if (Asserts.isEmpty(clzName)) {
            return null;
        }

        return clzCached.computeIfAbsent(clzName, k -> {
            try {
                return Class.forName(k);
            } catch (Throwable e) {
                throw new TypeConvertException("Failed to load class: " + clzName, e);
            }
        });
    }

    /////////////////


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
            throw new TypeConvertException("Read string from reader error", e);
        }

        String text = buf.toString();

        if (reader != null) {
            try {
                reader.close();
            } catch (Throwable e) {
                throw new TypeConvertException("Read string from reader error", e);
            }
        }

        return text;
    }

    private static final Map<Class<?>, Object> insCached = new ConcurrentHashMap<>();

    /**
     * 获取实体
     */
    public static Object getInstance(Class<?> clz) {
        return insCached.computeIfAbsent(clz, k -> newInstance(k));
    }

    /**
     * 新建实例
     */
    public static Object newInstance(Class<?> clz) throws ReflectionException {
        try {
            if (clz.isInterface()) {
                return null;
            } else {
                return clz.getDeclaredConstructor().newInstance();
            }
        } catch (Throwable e) {
            throw new ReflectionException("Instantiation failed: " + clz.getName(), e);
        }
    }

    public static Object newInstance(Constructor constructor, Object[] args) throws ReflectionException {
        if (constructor == null) {
            throw new IllegalArgumentException("constructor is null");
        }

        try {
            return constructor.newInstance(args);
        } catch (Throwable e) {
            throw new ReflectionException("Instantiation failed: " + constructor.getDeclaringClass().getName(), e);
        }
    }
}