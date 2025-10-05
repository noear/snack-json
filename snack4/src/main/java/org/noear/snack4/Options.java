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

import org.noear.snack4.codec.CodecLib;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.codec.ObjectEncoder;
import org.noear.snack4.codec.ObjectFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * JSON 处理选项（线程安全配置）
 */
public final class Options {
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
    public static DateFormat DEF_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 默认选项实例
     */
    private static final Options DEFAULT = new Options();

    //编码仓库
    private final CodecLib codecLib;
    // 特性开关（使用位掩码存储）
    private int enabledFeatures;
    // 时间格式
    private  DateFormat dateFormat;

    // 读取最大深度
    private  int readMaxDepth;

    // 书写缩进
    private  String writeIndent;


    private Set<Class<?>> allowedClasses = new HashSet<>();

    public void allowClass(Class<?> clazz) {
        allowedClasses.add(clazz);
    }

    public Options() {
        // 合并特性开关
        for (Feature feat : Feature.values()) {
            if (feat.enabledByDefault()) {
                enabledFeatures |= feat.mask();
            }
        }

        // 通用配置
        this.dateFormat = DEF_DATETIME_FORMAT;
        this.codecLib = CodecLib.newInstance();

        // 输入配置
        this.readMaxDepth = 512;

        // 输出配置
        this.writeIndent = "  ";
    }

    /**
     * 是否启用指定特性
     */
    public boolean isFeatureEnabled(Feature feature) {
        return (enabledFeatures & feature.mask()) != 0;
    }

    /**
     * 获取日期格式
     */
    public DateFormat getDateFormat() {
        return dateFormat;
    }

    /**
     * 获取解码器
     */
    public ObjectDecoder<?> getDecoder(Class<?> clazz) {
        return codecLib.getDecoder(clazz);
    }

    /**
     * 获取编码器
     *
     */
    public ObjectEncoder<?> getEncoder(Object value) {
        return codecLib.getEncoder(value);
    }

    /**
     * 获取对象工厂
     *
     */
    public ObjectFactory<?> getFactory(Class<?> clazz) {
        return codecLib.getFactory(clazz);
    }

    /**
     * 获取最大解析深度
     */
    public int getReadMaxDepth() {
        return readMaxDepth;
    }

    /**
     * 获取缩进字符串
     */
    public String getWriteIndent() {
        return writeIndent;
    }


    /**
     * 添加特性
     */
    public Options enableFeature(Feature feature) {
        enabledFeatures |= feature.mask();
        return this;
    }

    /**
     * 移除特性
     */
    public Options disableFeature(Feature feature) {
        enabledFeatures &= ~ feature.mask();
        return this;
    }


    /**
     * 设置日期格式
     */
    public Options dateFormat(DateFormat format) {
        this.dateFormat = format;
        return this;
    }

    /**
     * 注册自定义解码器
     */
    public <T> Options addDecoder(Class<T> type, ObjectDecoder<T> decoder) {
        codecLib.addDecoder(type, decoder);
        return this;
    }

    /**
     * 注册自定义编码器
     */
    public <T> Options addEncoder(Class<T> type, ObjectEncoder<T> encoder) {
        codecLib.addEncoder(type, encoder);
        return this;
    }

    public <T> Options addFactory(Class<T> type, ObjectFactory<T> factory) {
        codecLib.addFactory(type, factory);
        return this;
    }

    /**
     * 设置最大解析深度
     */
    public Options readMaxDepth(int depth) {
        this.readMaxDepth = depth;
        return this;
    }

    /**
     * 设置缩进字符串
     */
    public Options writeIndent(String indent) {
        this.writeIndent = indent;
        return this;
    }


    /**
     * 获取默认选项
     */
    public static Options def() {
        return DEFAULT;
    }

    public static Options of(Feature... features) {
        Options tmp = new Options();
        for (Feature f : features) {
            tmp.enableFeature(f);
        }
        return tmp;
    }
}