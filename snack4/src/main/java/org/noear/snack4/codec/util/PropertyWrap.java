package org.noear.snack4.codec.util;


/**
 *
 * @author noear 2025/10/8 created
 *
 */
public class PropertyWrap {
    private final String name;

    private PropertyFieldWrap fieldWrap;
    private PropertyMethodWrap getterWrap;
    private PropertyMethodWrap setterWrap;

    public PropertyWrap(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public PropertyFieldWrap getFieldWrap() {
        return fieldWrap;
    }

    public PropertyMethodWrap getGetterWrap() {
        return getterWrap;
    }

    public PropertyMethodWrap getSetterWrap() {
        return setterWrap;
    }

    /// //////////

    protected void setFieldWrap(PropertyFieldWrap fieldWrap) {
        this.fieldWrap = fieldWrap;
    }

    protected void setGetterWrap(PropertyMethodWrap getterWrap) {
        this.getterWrap = getterWrap;
    }

    protected void setSetterWrap(PropertyMethodWrap setterWrap) {
        this.setterWrap = setterWrap;
    }
}
