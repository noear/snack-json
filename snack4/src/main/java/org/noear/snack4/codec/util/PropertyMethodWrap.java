package org.noear.snack4.codec.util;

import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.annotation.ONodeAttrHolder;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

/**
 *
 * @author noear 2025/10/8 created
 *
 */
public class PropertyMethodWrap implements Property {
    private final Method property;
    private final TypeWrap propertyTypeWrap;

    private final ONodeAttrHolder attr;
    private final String name;

    private boolean isReadMode;

    public PropertyMethodWrap(TypeWrap owner, Method property) {
        if (property.isAccessible() == false) {
            property.setAccessible(true);
        }

        this.property = property;

        if (property.getReturnType() != void.class) {
            //getter
            this.isReadMode = true;
            this.propertyTypeWrap = TypeWrap.from(GenericUtil.reviewType(property.getGenericReturnType(), getGenericInfo(owner, property)));
        } else {
            //setter
            this.propertyTypeWrap = TypeWrap.from(GenericUtil.reviewType(property.getGenericParameterTypes()[0], getGenericInfo(owner, property)));
        }

        ONodeAttr attrAnno = property.getAnnotation(ONodeAttr.class);

        if (attrAnno != null) {
            this.name = attrAnno.name();
            this.attr = new ONodeAttrHolder(attrAnno, false);
        } else {
            String nameTmp = property.getName().substring(3);
            this.name = nameTmp.substring(0, 1).toLowerCase() + nameTmp.substring(1);
            this.attr = new ONodeAttrHolder(null, false);
        }
    }

    @Override
    public Object getValue(Object target) throws Exception {
        if (isReadMode) {
            return property.invoke(target);
        } else {
            return null;
        }
    }

    @Override
    public void setValue(Object target, Object value) throws Exception {
        if (isReadMode == false) {
            property.invoke(target, value);
        }
    }

    @Override
    public TypeWrap getTypeWrap() {
        return propertyTypeWrap;
    }

    @Override
    public ONodeAttrHolder getAttr() {
        return attr;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return property.toString();
    }

    private static Map<String, Type> getGenericInfo(TypeWrap owner, Method method) {
        if (method.getDeclaringClass() == owner.getType()) {
            return owner.getGenericInfo();
        } else {
            Type superType = GenericUtil.reviewType(owner.getType().getGenericSuperclass(), owner.getGenericInfo());
            return getGenericInfo(TypeWrap.from(superType), method);
        }
    }
}