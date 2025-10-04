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

import org.noear.snack4.codec.NodeDecoder;
import org.noear.snack4.codec.NodeEncoder;
import org.noear.snack4.codec.ObjectFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * JSON 处理选项（线程安全配置）
 */
public final class Options {
    /**
     * 默认选项实例
     */
    private static final Options DEFAULT = new Builder().build();

    // 特性开关（使用位掩码存储）
    private final int enabledFeatures;

    // 通用配置
    private final DateFormat _dateFormat;
    private final Map<Class<?>, NodeDecoder<?>> _decoderRegistry;
    private final Map<Class<?>, NodeEncoder<?>> _encoderRegistry;
    private final Map<Class<?>, ObjectFactory<?>> _objectFactory;

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
        this._decoderRegistry = Collections.unmodifiableMap(builder.decoderRegistry);
        this._encoderRegistry = Collections.unmodifiableMap(builder.encoderRegistry);
        this._objectFactory = Collections.unmodifiableMap(builder.objectFactory);

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
     * 获取自定义编解码器注册表
     */
    public NodeDecoder<?> getNodeDecoder(Class<?> clazz) {
        return _decoderRegistry.get(clazz);
    }

    public NodeEncoder<?> getNodeEncoder(Class<?> clazz) {
        return _encoderRegistry.get(clazz);
    }

    public ObjectFactory<?> getObjectFactory(Class<?> clazz) {
        return _objectFactory.get(clazz);
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
        private final Map<Class<?>, NodeDecoder<?>> decoderRegistry = new HashMap<>();
        private final Map<Class<?>, NodeEncoder<?>> encoderRegistry = new HashMap<>();
        private final Map<Class<?>, ObjectFactory<?>> objectFactory = new HashMap<>();

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
         * 启用/禁用指定特性
         */
        public Builder enable(Feature feature) {
            return enable(feature, true);
        }

        public Builder disable(Feature feature) {
            return enable(feature, false);
        }

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
        public <T> Builder addNodeDecoder(Class<T> type, NodeDecoder<T> decoder) {
            decoderRegistry.put(type, decoder);
            return this;
        }

        /**
         * 注册自定义编码器
         */
        public <T> Builder addNodeEncoder(Class<T> type, NodeEncoder<T> encoder) {
            encoderRegistry.put(type, encoder);
            return this;
        }

        public <T> Builder addObjectFactory(Class<T> type, ObjectFactory<T> factory) {
            objectFactory.put(type, factory);
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