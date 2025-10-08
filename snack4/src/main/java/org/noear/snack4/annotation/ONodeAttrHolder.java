package org.noear.snack4.annotation;

import org.noear.snack4.Feature;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.codec.ObjectEncoder;
import org.noear.snack4.codec.util.ClassUtil;

/**
 *
 * @author noear 2025/10/8 created
 */
public class ONodeAttrHolder {
    private String name;
    private String format;
    private String timezone;

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
            format = attrAnno.format();
            timezone = attrAnno.timezone();

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

    public String getFormat() {
        return format;
    }

    public String getTimezone() {
        return timezone;
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