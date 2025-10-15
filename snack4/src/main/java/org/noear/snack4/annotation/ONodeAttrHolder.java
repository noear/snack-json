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

import org.noear.snack4.core.Feature;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.codec.ObjectEncoder;
import org.noear.snack4.codec.util.ClassUtil;
import org.noear.snack4.codec.util.DateUtil;
import org.noear.snack4.core.util.Asserts;

import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author noear 2025/10/8 created
 * @since 4.0
 */
public class ONodeAttrHolder {
    private String name;
    private String description;

    private String format;
    private TimeZone timezone;

    private boolean flat;

    private boolean encode = true;
    private boolean decode = true;
    private ObjectEncoder encoder;
    private ObjectDecoder decoder;
    private int featuresValue;

    public ONodeAttrHolder(ONodeAttr attrAnno, boolean isTransient) {
        if (attrAnno != null) {
            name = attrAnno.name();
            description = attrAnno.description();

            format = attrAnno.format();
            if (Asserts.isNotEmpty(attrAnno.timezone())) {
                timezone = TimeZone.getTimeZone(ZoneId.of(attrAnno.timezone()));
            }

            flat = attrAnno.flat();
            encode = attrAnno.encode();
            decode = attrAnno.decode();

            if (attrAnno.encoder().isInterface() == false) {
                encoder = ClassUtil.newInstance(attrAnno.encoder());
            }

            if (attrAnno.decoder().isInterface() == false) {
                decoder = ClassUtil.newInstance(attrAnno.decoder());
            }

            featuresValue = Feature.addFeatures(0, attrAnno.features());
        }

        if (isTransient) {
            encode = false;
            decode = false;
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFormat() {
        return format;
    }

    public TimeZone getTimezone() {
        return timezone;
    }

    public String formatDate(Date value) {
        if (getTimezone() != null) {
            return DateUtil.format(value, getFormat(), getTimezone());
        } else {
            return DateUtil.format(value, getFormat());
        }
    }

    public boolean isFlat() {
        return flat;
    }

    public boolean hasFeature(Feature feature) {
        return Feature.hasFeature(featuresValue, feature);
    }

    public boolean isEncode() {
        return encode;
    }

    public boolean isDecode() {
        return decode;
    }

    public ObjectEncoder getEncoder() {
        return encoder;
    }

    public ObjectDecoder getDecoder() {
        return decoder;
    }
}