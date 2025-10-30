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
package org.noear.snack4.json.util;

/**
 *
 * @author noear 2025/10/30 created
 * @since 4.0
 */
public class NameUtil {
    public static String toSmlSnakeStyle(StringBuilder buf, String originName) {
        if (originName == null || originName.isEmpty()) {
            return originName;
        }

        buf.setLength(0);

        for (int i = 0; i < originName.length(); i++) {
            char c = originName.charAt(i);

            if (Character.isUpperCase(c)) {
                // 1. 如果不是第一个字符
                if (i > 0) {
                    char prevC = originName.charAt(i - 1);

                    // 2. 检查前一个字符是否为下划线。如果是，不加下划线。
                    if (prevC != '_') {
                        // 3. 核心优化：处理连续大写字母 (缩写词)
                        // 只有当前一个字符是小写 (prevC 是 'a'-'z')，或者
                        // 它是缩写词的最后一个大写字母 (下一个字符存在且是小写) 时，才添加下划线。
                        if (Character.isLowerCase(prevC) ||
                                (i + 1 < originName.length() && Character.isLowerCase(originName.charAt(i + 1)))) {
                            buf.append('_');
                        }
                    }
                }

                // 统一转换为小写
                buf.append(Character.toLowerCase(c));
            } else {
                buf.append(c);
            }
        }

        return buf.toString();
    }

    public static String toSmlCamelStyle(StringBuilder buf, String originName) {
        if (originName == null || originName.isEmpty()) {
            return originName;
        }

        if (originName.indexOf('_') < 0) {
            return originName;
        }

        buf.setLength(0);
        boolean nextCharToUpper = false;
        boolean firstChar = true; // 新增标记，用于控制首字母小写

        for (char c : originName.toCharArray()) {
            if (c == '_') {
                nextCharToUpper = true;
            } else {
                if (firstChar) {
                    // 确保首个有效字符是小写
                    buf.append(Character.toLowerCase(c));
                    firstChar = false;
                    nextCharToUpper = false; // 无论原先是否为 true，首字母后都重置
                } else if (nextCharToUpper) {
                    // 遇到下划线后的字符，转大写
                    buf.append(Character.toUpperCase(c));
                    nextCharToUpper = false;
                } else {
                    // 其他字符，转小写 (保持原样，因为我们依赖 toLowerCase() 后的输入或保持原样)
                    // 为了鲁棒性，此处改为转小写，防止如 "user_ID" 这种非标准输入导致 "userID"
                    buf.append(Character.toLowerCase(c));
                }
            }
        }

        return buf.toString();
    }
}