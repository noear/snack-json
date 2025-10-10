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
 *
 * @author noear 2019/2/12 created
 * @since 4.0
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
     * 读取时允许使用注释
     */
    Read_AllowComment(false),

    /**
     * 读取时禁止单引号字符串
     */
    Read_DisableSingleQuotes(false),

    /**
     * 读取时禁止未用引号包裹的键名
     */
    Read_DisableUnquotedKeys(false),

    /**
     * 读取时允许未空的键名
     */
    Read_AllowEmptyKeys(false),

    /**
     * 读取时允许JavaScript风格的十六进制数字 (如 0x1F)
     */
    Read_AllowHexNumbers(false),

    /**
     * 读取时允许零开头的数字
     */
    Read_AllowZeroLeadingNumbers(false),

    /**
     * 读取时允许特殊浮点值 (Infinity, -Infinity, NaN)
     */
    Read_AllowSpecialFloats(false),

    /**
     * 读取时自动转换字段命名风格（默认不转换）
     */
    Read_ConvertUnderlineStyle(false),

    /**
     * 读取时自动展开行内JSON字符串 (如 {"data": "{\"id\":1}"} )
     */
    Read_UnwrapJsonString(false),

    /**
     * 读取时允许对任何字符进行反斜杠转义
     */
    Read_AllowBackslashEscapingAnyCharacter(false),

    /**
     * 读取时允许无效的转义符
     */
    Read_AllowInvalidEscapeCharacter(false),

    /**
     * 读取时允许未编码的控制符
     */
    Read_AllowUnescapedControlCharacters(false),

    /**
     * 读取大数字时使用字符串模式（避免精度丢失）
     */
    Read_UseBigNumberMode(false),

    /**
     * 读取时用浏览器兼容模式（转义非 ASCII 字符）
     */
    Read_BrowserCompatible(false),

    /**
     * 读取时用日期格式化（默认使用时间戳）
     */
    Read_UseDateFormat(false),

    /**
     * 读取时允许使用获取器
     */
    Read_AllowUseGetter(false),

    /**
     * 读取时只能使用获取器
     */
    Read_OnlyUseGetter(false),

    /**
     * 读取时禁止类名读取
     */
    Read_DisableClassName(false),


    //-----------------------------
    // 写入（序列化）
    //-----------------------------

    /**
     * 写入用无引号字段名
     *
     */
    Write_UnquotedFieldNames(false),

    /**
     * 写入 null
     */
    Write_Nulls(false),

    /**
     * 写入字符串为 null 时转为空
     */
    Write_NullStringAsEmpty(false),

    /**
     * 写入布尔为 null 时转为 false
     *
     */
    Write_NullBooleanAsFalse(false),

    /**
     * 写入数字为 null 时转为 0
     *
     */
    Write_NullNumberAsZero(false),

    /**
     * 写入允许使用设置器（默认为字段模式）
     */
    Write_AllowUseSetter(false),
    /**
     * 写入只能使用设置器
     *
     */
    Write_OnlyUseOnlySetter(false),

    /**
     * 写入时使用漂亮格式（带缩进和换行）
     */
    Write_PrettyFormat(false),

    /**
     * 写入时使用单引号
     */
    Write_UseSingleQuotes(false),

    /**
     * 写入时字段使用下划线风格
     */
    Write_UseUnderlineStyle(false),

    /**
     * 写入时枚举使用名称（默认使用名称）
     */
    Write_EnumUsingName(true),

    /**
     * 写入时枚举使用 toString
     */
    Write_EnumUsingToString(false),

    /**
     * 写入类名
     */
    Write_ClassName(false),

    /**
     * 写入数组类名
     */
    Write_ArrayClassName(false),

    /**
     * 写入映射类名
     */
    Write_MapClassName(false),

    /**
     * 不写入根类名
     */
    Write_NotRootClassName(false),

    /**
     * 写入使用原始反斜杠（`\\` 不会转为 `\\\\`）
     */
    Write_UseRawBackslash(false),

    /**
     * 写入兼容浏览器显示（转义非 ASCII 字符）
     */
    Write_BrowserCompatible(false),

    /**
     * 写入使用日期格式化（默认使用时间戳）
     */
    Write_UseDateFormat(false),

    /**
     * 写入数字时使用字符串模式
     */
    Write_NumbersAsString(false),

    /**
     * 写入大数字时使用字符串模式（避免精度丢失）
     */
    Write_BigNumbersAsString(false),
    /**
     * 写入数字类型
     *
     */
    Write_NumberTypeSuffix(false),
    ;


    private final boolean _default;
    private final int _mask;

    Feature(boolean def) {
        _default = def;
        _mask = (1 << ordinal());
    }

    public boolean enabledByDefault() {
        return _default;
    }

    public int mask() {
        return _mask;
    }

    public static int DEFAULT() {
        int features = 0;
        // 合并特性开关
        for (Feature feat : Feature.values()) {
            if (feat.enabledByDefault()) {
                features |= feat.mask();
            }
        }
        return features;
    }

    public static int addFeature(int ref, Feature... features) {
        for (Feature feature : features) {
            ref |= feature.mask();
        }
        return ref;
    }

    public static int removeFeature(int ref, Feature... features) {
        for (Feature feature : features) {
            ref &= ~feature.mask();
        }
        return ref;
    }

    public static boolean hasFeature(int ref, Feature feature) {
        return (ref & feature.mask()) != 0;
    }
}