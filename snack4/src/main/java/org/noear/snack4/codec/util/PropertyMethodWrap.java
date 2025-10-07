package org.noear.snack4.codec.util;

import org.noear.snack4.Feature;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.codec.ObjectEncoder;
import org.noear.snack4.exception.AnnotationProcessException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

/**
 *
 * @author noear 2025/10/8 created
 *
 */
public class PropertyMethodWrap implements Property{
    private final TypeWrap owner;
    private final Method property;
    private final TypeWrap propertyTypeWrap;
    private final ONodeAttr attr;

    private final String name;
    private boolean readOnly;
    private boolean asString;
    private boolean flat;

    private boolean serialize = true;
    private boolean deserialize = true;
    private ObjectEncoder serializeEncoder;
    private ObjectDecoder deserializeDecoder;
    private int deserializeFeaturesValue;
    private int serializeFeaturesValue;

    public PropertyMethodWrap(TypeWrap owner, Method property) {
        if (property.isAccessible() == false) {
            property.setAccessible(true);
        }

        this.owner = owner;
        this.property = property;

        if(property.getReturnType() != void.class) {
            //getter
            this.readOnly = true;
            this.propertyTypeWrap = TypeWrap.from(GenericUtil.reviewType(property.getGenericReturnType(), getGenericInfo(owner, property)));
        } else {
            //setter
            this.propertyTypeWrap = TypeWrap.from(GenericUtil.reviewType(property.getGenericParameterTypes()[0], getGenericInfo(owner, property)));
        }

        this.attr = property.getAnnotation(ONodeAttr.class);

        if (attr != null) {
            name = attr.name();
            asString = attr.asString();
            flat = attr.flat();

            serialize = attr.serialize();
            deserialize = attr.deserialize();

            if (attr.serializeEncoder().isInterface() == false) {
                serializeEncoder = ClassUtil.newInstance(attr.serializeEncoder(), e -> new AnnotationProcessException("Property to create decoder for field: " + property.getName(), e));
            }

            if (attr.deserializeDecoder().isInterface() == false) {
                deserializeDecoder = ClassUtil.newInstance(attr.deserializeDecoder(), e -> new AnnotationProcessException("Property to create encoder for field: " + property.getName(), e));
            }

            deserializeFeaturesValue = Feature.addFeature(0, attr.deserializeFeatures());
            serializeFeaturesValue = Feature.addFeature(0, attr.serializeFeatures());
        } else {
            String nameTmp = property.getName().substring(3);
            name = nameTmp.substring(0, 1).toLowerCase() + nameTmp.substring(1);
        }
    }

    private static Map<String, Type> getGenericInfo(TypeWrap owner, Method method) {
        if (method.getDeclaringClass() == owner.getType()) {
            return owner.getGenericInfo();
        } else {
            Type superType = GenericUtil.reviewType(owner.getType().getGenericSuperclass(), owner.getGenericInfo());
            return getGenericInfo(TypeWrap.from(superType), method);
        }
    }

    public Object getValue(Object target) throws Exception {
        if(readOnly) {
            return property.invoke(target);
        } else{
            return null;
        }
    }

    public void setValue(Object target, Object value) throws Exception {
        if (readOnly == false) {
            property.invoke(target, value);
        }
    }


    public Method getMethod() {
        return property;
    }

    public TypeWrap getTypeWrap() {
        return propertyTypeWrap;
    }

    public ONodeAttr getAttr() {
        return attr;
    }

    public String getName() {
        return name;
    }

    public boolean isAsString() {
        return asString;
    }

    public boolean isFlat() {
        return flat;
    }

    /// //////

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

    @Override
    public String toString() {
        return property.toString();
    }
}
