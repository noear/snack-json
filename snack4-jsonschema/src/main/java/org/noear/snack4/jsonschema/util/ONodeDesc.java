package org.noear.snack4.jsonschema.util;

import java.lang.reflect.Type;

/**
 *
 * @author noear 2025/10/9 created
 *
 */
public class ONodeDesc {
    private final String name;
    private final Type type;
    private final boolean required;
    private final String description;

    public ONodeDesc(String name, Type type, boolean required, String description) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.description = (description == null ? "" : description);
    }

    /**
     * 参数名字
     */
    public String name() {
        return name;
    }

    /**
     * 参数类型
     */
    public Type type() {
        return type;
    }

    /**
     * 参数描述
     */
    public String description() {
        return description;
    }

    /**
     * 是否必须
     */
    public boolean required() {
        return required;
    }

    @Override
    public String toString() {
        return "NodeDesc{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", required=" + required +
                ", type=" + type +
                '}';
    }
}
