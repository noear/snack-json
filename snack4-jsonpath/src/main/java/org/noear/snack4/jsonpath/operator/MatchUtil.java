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
package org.noear.snack4.jsonpath.operator;

import org.noear.snack4.ONode;

/**
 *
 * @author noear 2025/10/12 created
 * @since 4.0
 */
public class MatchUtil {
    public static boolean isValueMatchPlus(ONode refere, ONode source) {
        if (source.isArray()) {
            for (ONode item : source.getArray()) {
                if (MatchUtil.isValueMatch(refere, item)) {
                    return true;
                }
            }
        } else {
            return MatchUtil.isValueMatch(refere, source);
        }

        return false;
    }

    public static boolean isValueMatch(ONode refere, ONode source) {
        if (refere.isArray()) {
            for(ONode ref : refere.getArray()) {
                if (isValueMatch(ref, source)) {
                    return true;
                }
            }
        }

        if (refere.isString()) {
            if (source.isString()) {
                return refere.getString().equals(source.getString());
            }
        } else if (refere.isNumber()) {
            if (source.isNumber()) {
                double itemValue = refere.getDouble();
                double expectedValue = source.getNumber().doubleValue();
                return itemValue == expectedValue;
            }
        } else if (refere.isBoolean()) {
            if (source.isBoolean()) {
                Boolean itemBool = refere.getBoolean();
                return itemBool == source.getBoolean();
            }
        } else if (refere.isNull()) {
            if (source.isNull()) {
                return true;
            }
        }

        return false;
    }
}
