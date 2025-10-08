package org.noear.snack4.codec.util;

import org.noear.snack4.annotation.ONodeAttrHolder;

/**
 *
 * @author noear 2025/10/8 created
 *
 */
public interface Property {
    Object getValue(Object target) throws Exception;

    void setValue(Object target, Object value) throws Exception;

    TypeWrap getTypeWrap();

    ONodeAttrHolder getAttr();

    String getName();
}