package org.noear.snack4.codec.util;

import org.noear.snack4.Feature;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.codec.ObjectEncoder;

/**
 *
 * @author noear 2025/10/8 created
 *
 */
public interface Property {
    Object getValue(Object target) throws Exception;
    void setValue(Object target, Object value) throws Exception;
    TypeWrap getTypeWrap();

    ONodeAttr getAttr();
    String getName();
    boolean isAsString();
    boolean isFlat();

    boolean isSerialize();
    boolean isDeserialize();
    boolean hasSerializeFeature(Feature feature);
    boolean hasDeserializeFeature(Feature feature);
    ObjectEncoder getSerializeEncoder();
    ObjectDecoder getDeserializeDecoder();
    String toString();
}
