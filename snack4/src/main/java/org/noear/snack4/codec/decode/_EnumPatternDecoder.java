package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.NodePatternDecoder;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class _EnumPatternDecoder implements NodePatternDecoder<Object> {

    @Override
    public boolean canDecode(Class<?> clazz) {
        return clazz.isEnum();
    }

    @Override
    public Object decode(Options opts, ONodeAttr attr, ONode node, Class<?> clazz) {
        String value = node.getString();
        try {
            return Enum.valueOf((Class<? extends Enum>) clazz, value);
        } catch (IllegalArgumentException e) {
            Enum[] constants = ((Class<? extends Enum>) clazz).getEnumConstants();
            String valid = Arrays.stream(constants)
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(
                    "Invalid enum value: '" + value + "'. Valid values: " + valid, e);
        }
    }
}