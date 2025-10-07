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
package org.noear.snack4.annotation;


import org.noear.snack4.Feature;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.codec.ObjectEncoder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author noear 2025/3/16 created
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ONodeAttr {
    /**
     * 别名
     */
    String name() default "";

    /**
     * 格式化
     */
    String format() default "";

    /**
     * 时区
     */
    String timezone() default "";

    /**
     * 作为字符串
     */
    boolean asString() default false;

    /**
     * 扁平化
     */
    boolean flat() default false;

    /**
     * 是否序列化
     */
    boolean serialize() default true;

    /**
     * 是否反序列化
     */
    boolean deserialize() default true;

    /**
     * 序列化特性
     */
    Feature[] serializeFeatures() default {};

    /**
     * 反序列化特性
     */
    Feature[] deserializeFeatures() default {};

    /**
     * 序列化编码器
     */
    Class<? extends ObjectEncoder> serializeEncoder() default ObjectEncoder.class;

    /**
     * 反序列化解码器
     */
    Class<? extends ObjectDecoder> deserializeDecoder() default ObjectDecoder.class;
}