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

import org.noear.snack4.codec.CodecRepository;
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
    public static String DEF_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";


    /**
     * 默认选项实例
     */
    private static final Options DEFAULT = new Builder().build();

    // 特性开关（使用位掩码存储）
    private final int enabledFeatures;

    // 通用配置
    private final DateFormat _dateFormat;
    private final CodecRepository  _codecRepository;

    // 输入配置
    private final int _maxDepth;

    // 输出配置
    private final String _indent;


    private Set<Class<?>> allowedClasses = new HashSet<>();

    public void allowClass(Class<?> clazz) {
        allowedClasses.add(clazz);
    }

    private Options(Builder builder) {
        // 合并特性开关
        int features = 0;
        for (Feature feat : Feature.values()) {
            if (builder.features.getOrDefault(feat, feat.enabledByDefault())) {
                features |= feat.mask();
            }
        }
        this.enabledFeatures = features;

        // 通用配置
        this._dateFormat = builder.dateFormat;
        this._codecRepository = builder.codecRepository;

        // 输入配置
        this._maxDepth = builder.maxDepth;

        // 输出配置
        this._indent = builder.indent;
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
        return _dateFormat;
    }

    /**
     * 获取解码器
     */
    public ObjectDecoder<?> getDecoder(Class<?> clazz) {
        return _codecRepository.getDecoder(clazz);
    }

    /**
     * 获取编码器
     *
     */
    public ObjectEncoder<?> getEncoder(Object value) {
        return _codecRepository.getEncoder(value);
    }

    /**
     * 获取对象工厂
     *
     */
    public ObjectFactory<?> getFactory(Class<?> clazz) {
        return _codecRepository.getFactory(clazz);
    }

    /**
     * 获取最大解析深度
     */
    public int getMaxDepth() {
        return _maxDepth;
    }

    /**
     * 获取缩进字符串
     */
    public String getIndent() {
        return _indent;
    }

    /**
     * 获取默认选项
     */
    public static Options def() {
        return DEFAULT;
    }

    public static Options of(Feature... features) {
        Builder tmp = new Builder();
        for (Feature f : features) {
            tmp.enable(f);
        }
        return tmp.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 选项建造者
     */
    public static class Builder {
        private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 特性开关存储
        private final EnumMap<Feature, Boolean> features = new EnumMap<>(Feature.class);

        // 通用配置
        private DateFormat dateFormat = DEFAULT_DATE_FORMAT;
        private final CodecRepository codecRepository = CodecRepository.newInstance();

        // 输入配置
        private int maxDepth = 512;

        // 输出配置
        private String indent = "  ";

        public Builder() {
            // 初始化默认特性
            for (Feature feat : Feature.values()) {
                features.put(feat, feat.enabledByDefault());
            }
        }

        /**
         * 启用特性
         */
        public Builder enable(Feature feature) {
            return enable(feature, true);
        }

        /**
         * 禁用特性
         */
        public Builder disable(Feature feature) {
            return enable(feature, false);
        }

        /**
         * 启用特性
         */
        public Builder enable(Feature feature, boolean state) {
            features.put(feature, state);
            return this;
        }

        /**
         * 设置日期格式
         */
        public Builder dateFormat(DateFormat format) {
            this.dateFormat = format;
            return this;
        }

        /**
         * 注册自定义解码器
         */
        public <T> Builder addDecoder(Class<T> type, ObjectDecoder<T> decoder) {
            codecRepository.addDecoder(type, decoder);
            return this;
        }

        /**
         * 注册自定义编码器
         */
        public <T> Builder addEncoder(Class<T> type, ObjectEncoder<T> encoder) {
            codecRepository.addEncoder(type, encoder);
            return this;
        }

        public <T> Builder addFactory(Class<T> type, ObjectFactory<T> factory) {
            codecRepository.addFactory(type, factory);
            return this;
        }

        /**
         * 设置最大解析深度
         */
        public Builder maxDepth(int depth) {
            this.maxDepth = depth;
            return this;
        }

        /**
         * 设置缩进字符串
         */
        public Builder indent(String indent) {
            this.indent = indent;
            return this;
        }

        /**
         * 构建最终选项
         */
        public Options build() {
            return new Options(this);
        }
    }
}