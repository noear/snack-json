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
package org.noear.snack4;

/**
 * JSON 特性枚举（按读写方向分类）
 */
public enum Feature {
    //-----------------------------
    // 读取（反序列化）
    //-----------------------------
    /**
     * 遇到未知属性时是否抛出异常
     */
    Read_FailOnUnknownProperties(false),

    /**
     * 是否允许使用注释
     */
    Read_AllowComment(false),
    /**
     * 允许单引号字符串
     */
    Read_AllowSingleQuotes(true),
    /**
     * 允许未用引号包裹的键名
     */
    Read_AllowUnquotedKeys(true),
    /**
     * 允许未空的键名
     */
    Read_AllowEmptyKeys(false),
    /**
     * 允许JavaScript风格的十六进制数字 (如 0x1F)
     */
    Read_AllowHexNumbers(false),
    /**
     * 允许零开头的数字
     */
    Read_AllowZeroLeadingNumbers(false),
    /**
     * 允许特殊浮点值 (Infinity, -Infinity, NaN)
     */
    Read_AllowSpecialFloats(false),
    /**
     * 自动转换字段命名风格（默认不转换）
     */
    Read_ConvertUnderlineStyle(false),
    /**
     * 自动解析日期格式 (支持多种格式)
     */
    Read_AutoParseDate(true),
    /**
     * 自动展开行内JSON字符串 (如 {"data": "{\"id\":1}"} )
     */
    Read_UnwrapJsonString(false),

    /**
     * 允许对任何字符进行反斜杠转义
     *
     */
    Read_AllowBackslashEscapingAnyCharacter(false),

    /**
     * 允许属性值省略引号 (自动推导类型)
     */
    Read_AllowAutoType(false),

    /**
     * 处理大数字时使用字符串模式（避免精度丢失）
     */
    Read_UseBigNumberMode(false),

    /**
     * 转义非 ASCII 字符
     */
    Read_EscapeNonAscii(false),

    /**
     * 使用日期格式化（默认使用时间戳）
     */
    Read_UseDateFormat(false),


    //-----------------------------
    // 写入（序列化）
    //-----------------------------
    /**
     * 序列化时是否输出 null 值
     */
    Write_SkipNullValue(false),

    /**
     * 输出时使用漂亮格式（带缩进）
     */
    Write_PrettyFormat(false),

    /**
     * 序列化时使用单引号
     */
    Write_UseSingleQuotes(false),

    /**
     * 输出字段使用下划线风格
     */
    Write_UseUnderlineStyle(false),

    /**
     * 输出枚举使用名称（默认使用名称）
     */
    Write_EnumUsingName(true),

    /**
     * 写入类名
     */
    Write_TypeName(false),

    /**
     * 处理大数字时使用字符串模式（避免精度丢失）
     */
    Write_UseBigNumberMode(false),

    /**
     * 转义非 ASCII 字符
     */
    Write_EscapeNonAscii(false),
    ;


    private final boolean _default;

    Feature(boolean def) {
        _default = def;
    }

    public boolean enabledByDefault() {
        return _default;
    }

    public int mask() {
        return (1 << ordinal());
    }
}