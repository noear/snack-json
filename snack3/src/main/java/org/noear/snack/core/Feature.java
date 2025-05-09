package org.noear.snack.core;

/**
 * 特性
 * */
public enum  Feature {
    /** 输出:为字段名加引号 */
    QuoteFieldNames,

    /** 存储:排序字段 */
    OrderedField,

    /** 输出:写入类名。反序列化是需用到 */
    WriteClassName,

    /** 输出:不写入根类名 */
    NotWriteRootClassName,

    /** 输出:写入数组类名。反序列化是需用到 */
    WriteArrayClassName,

    /** 输出:日期用Ticks */
    WriteDateUseTicks,

    /** 输出:日期用格式符控制 */
    WriteDateUseFormat,

    /** 输出:Bool用0或1替代 */
    WriteBoolUse01,

    /** 输出：数字用字符串 */
    WriteNumberUseString,

    /** 解析：整型使用长整 */
    ParseIntegerUseLong,

    /** 输出:浏览器安全处理（不输出<>） */
    BrowserSecure,

    /** 输出:浏览器兼容处理（将中文都会序列化为\\uXXXX格式，字节数会多一些） */
    BrowserCompatible,
    /**
     * 输出：传输兼容处理（不输出\）
     * */
    TransferCompatible,

    /** 输出:使用Enum的name输出 */
    EnumUsingName,

    /** 存储 or 输出:字符串Null时输出为空(get时用) */
    StringNullAsEmpty,
    /** 存储 or 输出:布尔Null时输出为空(get时用) */
    BooleanNullAsFalse,
    /** 存储 or 输出:数字Null时输出为空(get时用) */
    NumberNullAsZero,
    ArrayNullAsEmpty,

    /** 存储 or 输出:字符串字段初始化为空（返序列化时） */
    StringFieldInitEmpty,

    /** 输出：序列化Null */
    SerializeNulls,

    /** 输出：序列化Map/Null val */
    SerializeMapNullValues,

    /** 输出：使用单引号输出 */
    UseSingleQuotes,
    /**
     * 使用设置属性
     * */
    UseSetter,
    /**
     * 只使用设置属性
     * */
    UseOnlySetter,
    /**
     * 使用获取属性
     * */
    UseGetter,
    /**
     * 只使用获取属性
     * */
    UseOnlyGetter,

    /** 禁止线程缓存 */
    DisThreadLocal,

    /** 存储 or 读取：当 value is json string 时，自动转为ONode */
    StringJsonToNode,

    /** 读取 Double 使用 BigDecimal */
    StringDoubleToDecimal,
    /** 漂亮格式化 */
    PrettyFormat,
    /** 禁用类名读取 */
    DisableClassNameRead,
    /** 禁用集合默认值 */
    DisableCollectionDefaults,
    ;

    Feature(){
        code = (1 << ordinal());
    }

    /** 特性代码值 */
    public final int code;

    /** 特性启用情况 */
    public static boolean isEnabled(int features, Feature feature) {
        return (features & feature.code) != 0;
    }

    /** 特性配置：开启或禁用 */
    public static int config(int features, Feature feature, boolean enable) {
        if (enable) {
            features |= feature.code;
        } else {
            features &= ~feature.code;
        }

        return features;
    }

    /** 特性合并生成 */
    public static int of(Feature... features) {
        if (features == null) {
            return 0;
        }

        int value = 0;

        for (Feature feature: features) {
            value |= feature.code;
        }

        return value;
    }
}
