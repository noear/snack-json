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
import org.noear.snack4.codec.util.ClassUtil;
import org.noear.snack4.codec.util.DateUtil;
import org.noear.snack4.util.Asserts;

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

    private boolean serialize = true;
    private boolean deserialize = true;
    private ObjectEncoder serializeEncoder;
    private ObjectDecoder deserializeDecoder;
    private int deserializeFeaturesValue;
    private int serializeFeaturesValue;

    public ONodeAttrHolder(ONodeAttr attrAnno, boolean isTransient) {
        if (attrAnno != null) {
            name = attrAnno.name();
            description = attrAnno.description();

            format = attrAnno.format();
            if (Asserts.isNotEmpty(attrAnno.timezone())) {
                timezone = TimeZone.getTimeZone(ZoneId.of(attrAnno.timezone()));
            }

            flat = attrAnno.flat();
            serialize = attrAnno.serialize();
            deserialize = attrAnno.deserialize();

            if (attrAnno.serializeEncoder().isInterface() == false) {
                serializeEncoder = ClassUtil.newInstance(attrAnno.serializeEncoder());
            }

            if (attrAnno.deserializeDecoder().isInterface() == false) {
                deserializeDecoder = ClassUtil.newInstance(attrAnno.deserializeDecoder());
            }

            deserializeFeaturesValue = Feature.addFeature(0, attrAnno.deserializeFeatures());
            serializeFeaturesValue = Feature.addFeature(0, attrAnno.serializeFeatures());
        }

        if (isTransient) {
            serialize = false;
            deserialize = false;
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

    public boolean isSerialize() {
        return serialize;
    }

    public boolean isDeserialize() {
        return deserialize;
    }

    public boolean hasSerializeFeature(Feature feature) {
        return Feature.hasFeature(serializeFeaturesValue, feature);
    }

    public boolean hasDeserializeFeature(Feature feature) {
        return Feature.hasFeature(deserializeFeaturesValue, feature);
    }

    public ObjectEncoder getSerializeEncoder() {
        return serializeEncoder;
    }

    public ObjectDecoder getDeserializeDecoder() {
        return deserializeDecoder;
    }
}