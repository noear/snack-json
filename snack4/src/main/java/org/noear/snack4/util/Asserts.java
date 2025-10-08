/*
 * Copyright 2005-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.snack4.util;

import java.math.BigDecimal;
import java.math.BigInteger;
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
     * 检查数组是否为空
     *
     * @param s 集合
     */
    public static boolean isEmpty(Object[] s) {
        return s == null || s.length == 0;
    }

    /**
     * 检查映射是否为空
     *
     * @param s 集合
     */
    public static boolean isEmpty(Map s) {
        return s == null || s.size() == 0;
    }


    /**
     * 是否为整型
     */
    public static boolean isInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        int start = 0;
        if (str.charAt(0) == '-' || str.charAt(0) == '+') {
            if (str.length() == 1) {
                return false;
            }
            start = 1;
        }

        for (int i = start; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否为数字
     *
     */
    public static boolean isNumber(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        str = str.trim();
        int length = str.length();
        boolean hasDigit = false;
        boolean hasDot = false;
        boolean hasExp = false;
        boolean hasSign = false;

        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);

            if (c >= '0' && c <= '9') {
                hasDigit = true;
            } else if (c == '.') {
                // 小数点不能出现在指数部分或多次出现
                if (hasDot || hasExp) {
                    return false;
                }
                hasDot = true;
            } else if (c == 'e' || c == 'E') {
                // 指数符号前必须有数字且不能重复
                if (!hasDigit || hasExp) {
                    return false;
                }
                hasExp = true;
                hasDigit = false; // 重置，要求指数部分必须有数字
            } else if (c == '+' || c == '-') {
                // 符号只能出现在开头或指数符号后
                if (i != 0 && str.charAt(i - 1) != 'e' && str.charAt(i - 1) != 'E') {
                    return false;
                }
                hasSign = true;
            } else {
                return false; // 非法字符
            }
        }

        // 必须有数字且最后一个字符不能是e/E或+/-
        return hasDigit &&
                !(str.charAt(length - 1) == 'e' ||
                        str.charAt(length - 1) == 'E' ||
                        str.charAt(length - 1) == '+' ||
                        str.charAt(length - 1) == '-');
    }

    public static boolean isBigNumber(Number num) {
        return num instanceof Double ||
                num instanceof Long ||
                num instanceof BigInteger ||
                num instanceof BigDecimal;
    }
}
