package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectPatternDecoder;
import org.noear.snack4.codec.util.EnumWrap;
import org.noear.snack4.codec.util.TypeUtil;
import org.noear.snack4.SnackException;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class _EnumPatternDecoder implements ObjectPatternDecoder<Object> {

    @Override
    public boolean canDecode(Class<?> clazz) {
        return clazz.isEnum();
    }

    @Override
    public Object decode(DecodeContext ctx, ONode node) {
        EnumWrap ew = TypeUtil.createEnum(ctx.getType());

        //尝试自定义获取
        String valString = node.getString();

        Enum eItem;

        if (ew.hasCustom()) {
            //按自定义获取
            eItem = ew.getCustom(valString);
            // 获取不到则按名字获取
            if (eItem == null) {
                eItem = ew.get(valString);
            }
        } else {
            if (node.isString()) {
                //按名字获取
                eItem = ew.get(valString);
            } else {
                //按顺序位获取
                eItem = ew.get(node.getInt());
            }
        }

        if (eItem == null) {
            throw new SnackException(
                    "Deserialize failure for '" + ew.enumClass().getName() + "' from value: " + valString);
        }

        return eItem;
    }
}