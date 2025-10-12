package org.noear.snack4.jsonpath.operator;

import org.noear.snack4.ONode;

/**
 *
 * @author noear 2025/10/12 created
 *
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
            return refere.getArray().stream().anyMatch(one -> isValueMatch(one, source));
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
        }

        return false;
    }
}
