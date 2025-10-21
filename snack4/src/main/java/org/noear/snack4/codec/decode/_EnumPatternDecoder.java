/*
 * Copyright 2005-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectPatternDecoder;
import org.noear.snack4.codec.util.EnumWrap;
import org.noear.snack4.SnackException;

/**
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public class _EnumPatternDecoder implements ObjectPatternDecoder<Object> {

    @Override
    public boolean canDecode(Class<?> clazz) {
        return clazz.isEnum();
    }

    @Override
    public Object decode(DecodeContext ctx, ONode node) {
        EnumWrap ew = EnumWrap.from(ctx.getType());

        //尝试自定义获取
        String vs = node.getString();

        Enum eItem;

        if (ew.hasCustom()) {
            //按自定义获取
            eItem = ew.getCustom(vs);
            // 获取不到则按名字获取
            if (eItem == null) {
                eItem = ew.get(vs);
            }
        } else {
            if (node.isString()) {
                //按名字获取
                eItem = ew.get(vs);
            } else {
                //按顺序位获取
                eItem = ew.get(node.getInt());
            }
        }

        if (eItem == null) {
            throw new SnackException(
                    "Deserialize failure for '" + ew.enumClass().getName() + "' from value: " + vs);
        }

        return eItem;
    }
}