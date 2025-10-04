package org.noear.snack4.util;

import java.util.Collection;
import java.util.Map;

/**
 * @author noear 2025/5/4 created
 */
public class Asserts {
    public static boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }

    public static boolean isNotEmpty(String text) {
        return !isEmpty(text);
    }

    /**
     * 检查集合是否为空
     *
     * @param s 集合
     */
    public static boolean isEmpty(Collection s) {
        return s == null || s.size() == 0;
    }

    /**
     * 检查映射是否为空
     *
     * @param s 集合
     */
    public static boolean isEmpty(Map s) {
        return s == null || s.size() == 0;
    }
}
