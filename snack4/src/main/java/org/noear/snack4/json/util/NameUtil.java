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
                if (i > 0 && originName.charAt(i - 1) != '_') {
                    buf.append('_');
                }

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

        originName = originName.toLowerCase();

        boolean nextCharToUpper = false;
        buf.setLength(0);

        for (char c : originName.toCharArray()) {
            if (c == '_') {
                nextCharToUpper = true;
            } else {
                if (nextCharToUpper) {
                    buf.append(Character.toUpperCase(c));
                    nextCharToUpper = false;
                } else {
                    buf.append(c);
                }
            }
        }

        return buf.toString();
    }
}