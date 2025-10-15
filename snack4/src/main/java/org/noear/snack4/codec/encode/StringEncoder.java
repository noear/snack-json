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
import org.noear.snack4.codec.ObjectEncoder;

/**
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public class StringEncoder implements ObjectEncoder<String> {
    @Override
    public ONode encode(EncodeContext ctx, String value, ONode target) {
        if (ctx.getOptions().hasFeature(Feature.Read_UnwrapJsonString)) {
            if (value.length() > 1) {
                char c1 = value.charAt(0);
                char c2 = value.charAt(value.length() - 1);

                if ((c1 == '{' && c2 == '}') || (c1 == '[' && c2 == ']')) {
                    return ONode.ofJson(value, target.options());
                }
            }
        }

        return target.setValue(value);
    }
}