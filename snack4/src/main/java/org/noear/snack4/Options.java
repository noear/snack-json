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

import org.noear.snack4.codec.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * JSON 处理选项（线程安全配置）
 *
 * @author noear 2019/2/12 created
 * @since 4.0
 */
public final class Options {
    //默认类型的key
    public static final String DEF_TYPE_PROPERTY_NAME = "@type";
    //默认时区
    public static final TimeZone DEF_TIME_ZONE = TimeZone.getDefault();
    //默认偏移时区
    public static final ZoneOffset DEF_OFFSET = OffsetDateTime.now().getOffset();
    //默认地区
    public static final Locale DEF_LOCALE = Locale.getDefault();
    //默认时间格式器
    public static final String DEF_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    //默认特性
    public static final int DEF_FEATURES = 0;

    //默认选项（私有）
    public static final Options DEF_OPTIONS = new Options(true);
    public static final String DEF_UNSUPPORTED_HINT = "Read-only mode does not support modification.";

    //编码仓库
    private final CodecLib codecLib = CodecLib.newInstance();
    // 特性开关（使用位掩码存储）
    private int featuresValue = DEF_FEATURES;
    // 时间格式
    private String dateFormat = DEF_DATETIME_FORMAT;
    // 书写缩进
    private String writeIndent = "  ";
    // 类型属性名
    private String typePropertyName = DEF_TYPE_PROPERTY_NAME;
    // 类加载器
    private ClassLoader classLoader;
    // 允许安全类
    private Locale locale = DEF_LOCALE;

    private TimeZone timeZone = DEF_TIME_ZONE;

    private boolean RFC9535 = false;

    private final boolean readonly;

    private Options(boolean readonly) {
        this.readonly = readonly;
    }

    public boolean isRFC9535() {
        return RFC9535;
    }

    /**
     * 是否启用指定特性
     */
    public boolean hasFeature(Feature feature) {
        return Feature.hasFeature(this.featuresValue, feature);
    }

    /**
     * 加载类
     */
    public Class<?> loadClass(String className) {
        try {
            if (classLoader == null) {
                return Class.forName(className);
            } else {
                return classLoader.loadClass(className);
            }
        } catch (ClassNotFoundException e) {
            throw new SnackException("Failed to load class: " + className, e);
        }
    }


    public int getFeatures() {
        return featuresValue;
    }

    public Locale getLocale() {
        return locale;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * 获取日期格式
     */
    public String getDateFormat() {
        return dateFormat;
    }

    public String getTypePropertyName() {
        return typePropertyName;
    }

    /**
     * 获取解码器
     */
    public ObjectDecoder<?> getDecoder(Class<?> clazz) {
        return codecLib.getDecoder(clazz);
    }

    /**
     * 获取编码器
     */
    public ObjectEncoder<?> getEncoder(Object value) {
        return codecLib.getEncoder(value);
    }

    /**
     * 获取对象工厂
     */
    public ObjectFactory<?> getFactory(Class<?> clazz) {
        return codecLib.getFactory(clazz);
    }

    /**
     * 获取缩进字符串
     */
    public String getWriteIndent() {
        return writeIndent;
    }


    /// /////////////
    public Options RFC9535(boolean RFC9535) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        this.RFC9535 = RFC9535;
        return this;
    }

    /**
     * 设置日期格式
     */
    public Options dateFormat(String format) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        this.dateFormat = format;
        return this;
    }

    /**
     * 设置地区
     */
    public Options locale(Locale locale) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        this.locale = locale;
        return this;
    }

    /**
     * 设置时区
     */
    public Options timeZone(TimeZone timeZone) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        this.timeZone = timeZone;
        return this;
    }

    /**
     * 设置缩进字符串
     */
    public Options writeIndent(String indent) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        this.writeIndent = indent;
        return this;
    }

    /**
     * 设置类加载器
     */
    public Options classLoader(ClassLoader classLoader) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        this.classLoader = classLoader;
        return this;
    }

    /**
     * 添加特性
     */
    public Options addFeature(Feature... features) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        this.featuresValue = Feature.addFeature(this.featuresValue, features);
        return this;
    }

    /**
     * 移除特性
     */
    public Options removeFeature(Feature... features) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        this.featuresValue = Feature.removeFeature(this.featuresValue, features);
        return this;
    }


    /**
     * 注册自定义解码器
     */
    public <T> Options addDecoder(Class<T> type, ObjectDecoder<T> decoder) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        codecLib.addDecoder(type, decoder);
        return this;
    }

    /**
     * 注册自定义解码器
     */
    public <T> Options addDecoder(ObjectPatternDecoder<T> decoder) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        codecLib.addDecoder(decoder);
        return this;
    }

    /**
     * 注册自定义编码器
     */
    public <T> Options addEncoder(Class<T> type, ObjectEncoder<T> encoder) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        codecLib.addEncoder(type, encoder);
        return this;
    }

    /**
     * 注册自定义编码器
     */
    public <T> Options addEncoder(ObjectPatternEncoder<T> encoder) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        codecLib.addEncoder(encoder);
        return this;
    }

    /**
     * 注册自定义工厂
     */
    public <T> Options addFactory(Class<T> type, ObjectFactory<T> factory) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        codecLib.addFactory(type, factory);
        return this;
    }

    /**
     * 注册自定义工厂
     */
    public <T> Options addFactory(ObjectPatternFactory<T> factory) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        codecLib.addFactory(factory);
        return this;
    }

    public static Options of(Feature... features) {
        Options tmp = new Options(false);
        for (Feature f : features) {
            tmp.addFeature(f);
        }
        return tmp;
    }
}