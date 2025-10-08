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

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;
import org.noear.snack4.util.Asserts;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public class OffsetTimeEncoder implements ObjectEncoder<OffsetTime> {
    @Override
    public ONode encode(EncodeContext ctx, OffsetTime value, ONode target) {
        if (ctx.getAttr() != null) {
            if (Asserts.isNotEmpty(ctx.getAttr().getFormat())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ctx.getAttr().getFormat());
                return target.setValue(formatter.format(value));
            }
        }

        Instant instant = value.atDate(LocalDate.of(1970, 1, 1)).toInstant();
        return target.setValue(Date.from(instant));
    }
}
