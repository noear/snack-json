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
package org.noear.snack4.codec.encode;

import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectPatternEncoder;
import org.noear.snack4.util.Asserts;

/**
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public class _NumberPatternEncoder implements ObjectPatternEncoder<Number> {
    @Override
    public boolean canEncode(Object value) {
        return value instanceof Number;
    }

    @Override
    public ONode encode(EncodeContext ctx, Number value, ONode target) {
        if (ctx.getAttr() != null) {
            if (ctx.getAttr().hasFeature(Feature.Write_NumbersAsString)) {
                return target.setValue(value.toString());
            }

            if (ctx.getAttr().hasFeature(Feature.Write_BigNumbersAsString) && Asserts.isBigNumber(value)) {
                return target.setValue(value.toString());
            }

            if (ctx.getAttr().hasFeature(Feature.Write_LongAsString) && value instanceof Long) {
                return target.setValue(value.toString());
            }
        }

        return target.setValue(value);
    }
}