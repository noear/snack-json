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
import org.noear.snack4.codec.ObjectPatternEncoder;
import org.noear.snack4.SnackException;

import java.io.Reader;
import java.sql.Clob;

/**
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public class _ClobPatternEncoder implements ObjectPatternEncoder<Clob> {
    @Override
    public boolean canEncode(Object value) {
        return value instanceof Clob;
    }

    @Override
    public ONode encode(EncodeContext ctx, Clob value, ONode target) {
        return target.setValue(clobToString(value));
    }

    static String clobToString(Clob clob) {
        StringBuilder sb = new StringBuilder();

        try (Reader reader = clob.getCharacterStream()) {
            char[] chars = new char[1024];
            int size = 0;

            while ((size = reader.read(chars, 0, chars.length)) != -1) {
                sb.append(chars, 0, size);
            }
        } catch (Throwable e) {
            throw new SnackException("Clob read fail!", e);
        }

        return sb.toString();
    }
}