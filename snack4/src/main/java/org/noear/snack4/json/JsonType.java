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
package org.noear.snack4.json;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author noear 2025/3/16 created
 */
public enum JsonType {
    Undefined,
    Null,

    Boolean,
    Number,
    String,
    Date,

    Array,
    Object,
    ;

    @Override
    public String toString() {
        return getTypeName(this);
    }

    public static String getTypeName(JsonType type) {
        switch (type) {
            case Null:
                return "null";
            case Boolean:
                return "boolean";
            case Number:
                return "number";
            case String:
                return "string";
            case Date:
                return "date";
            case Array:
                return "array";
            case Object:
                return "object";
            default:
                return "unknown";
        }
    }

    public static boolean isValue(JsonType type) {
        return type.ordinal() > Null.ordinal() && type.ordinal() < Array.ordinal();
    }

    public static JsonType resolveType(Object value) {
        if (value == null) return JsonType.Null;
        if (value instanceof Boolean) return JsonType.Boolean;
        if (value instanceof Number) return JsonType.Number;
        if (value instanceof String) return JsonType.String;
        if (value instanceof Date) return JsonType.Date;
        if (value instanceof List) return JsonType.Array;
        if (value instanceof Map) return JsonType.Object;

        throw new IllegalArgumentException("Unsupported type");
    }

    public static JsonType resolveValueType(Object value) {
        if (value == null) return JsonType.Null;
        if (value instanceof Boolean) return JsonType.Boolean;
        if (value instanceof Number) return JsonType.Number;
        if (value instanceof String) return JsonType.String;
        if (value instanceof Date) return JsonType.Date;

        throw new IllegalArgumentException("Unsupported value type");
    }
}