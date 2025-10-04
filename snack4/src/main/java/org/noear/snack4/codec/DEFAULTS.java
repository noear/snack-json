package org.noear.snack4.codec;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 默认值
 * */
public class DEFAULTS {
    /**
     * 默认类型的key
     */
    public static final String DEF_TYPE_PROPERTY_NAME = "@type";

    /**
     * 默认时区
     */
    public static final TimeZone DEF_TIME_ZONE = TimeZone.getDefault();
    /**
     * 默认偏移时区
     */
    public static final ZoneOffset DEF_OFFSET = OffsetDateTime.now().getOffset();
    /**
     * 默认地区
     */
    public static final Locale DEF_LOCALE = Locale.getDefault();
    /**
     * 默认时间格式器
     */
    public static String DEF_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
}
