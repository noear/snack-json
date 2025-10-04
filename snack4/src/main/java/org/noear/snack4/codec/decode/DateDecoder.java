package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.NodeDecoder;
import org.noear.snack4.codec.util.DateUtil;
import org.noear.snack4.exception.TypeConvertException;

import java.util.Date;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class DateDecoder implements NodeDecoder<Date> {
    @Override
    public Date decode(Options opts, ONodeAttr attr, ONode node, Class<?> clazz) {
        if (node.isDate()) {
            return node.getDate();
        } else if (node.isNumber()) {
            return new Date(node.getLong());
        } else if (node.isString()) {
            try {
                return DateUtil.parse(node.getString());
            } catch (Exception ex) {
                throw new TypeConvertException("Cannot be converted to Date: " + node, ex);
            }
        } else {
            throw new TypeConvertException("Cannot be converted to Date: " + node);
        }
    }
}