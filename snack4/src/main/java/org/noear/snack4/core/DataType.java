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
package org.noear.snack4.core;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 数据类型
 *
 * @author noear 2025/3/16 created
 * @since 4.0
 */
public enum DataType {
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

    public static String getTypeName(DataType type) {
        switch (type) {
            case Undefined:
                return "undefined";
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

    public static boolean isValue(DataType type) {
        return type.ordinal() > Null.ordinal() && type.ordinal() < Array.ordinal();
    }

    public static DataType resolveType(Object value) {
        if (value == null) return DataType.Null;
        if (value instanceof Boolean) return DataType.Boolean;
        if (value instanceof Number) return DataType.Number;
        if (value instanceof String) return DataType.String;
        if (value instanceof Date) return DataType.Date;
        if (value instanceof List) return DataType.Array;
        if (value instanceof Map) return DataType.Object;

        throw new IllegalArgumentException("Unsupported type");
    }

    public static DataType resolveValueType(Object value) {
        if (value == null) return DataType.Null;
        if (value instanceof Boolean) return DataType.Boolean;
        if (value instanceof Number) return DataType.Number;
        if (value instanceof String) return DataType.String;
        if (value instanceof Date) return DataType.Date;

        throw new IllegalArgumentException("Unsupported value type");
    }
}