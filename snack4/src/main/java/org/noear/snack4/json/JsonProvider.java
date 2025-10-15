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
package org.noear.snack4.json;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;

import java.io.*;

/**
 * Json 能力提供者
 *
 * @author noear 2025/10/15 created
 * @since 4.0
 */
@FunctionalInterface
public interface JsonProvider {
    default ONode read(String text, Options opts) throws IOException {
        return read(new StringReader(text), opts);
    }

    default String write(ONode node, Options opts) throws IOException {
        StringWriter writer = new StringWriter();
        write(node, opts, writer);
        return writer.toString();
    }

    ///

    default ONode read(Reader reader, Options opts) throws IOException {
        throw new UnsupportedOperationException(warnHint());
    }

    default void write(ONode node, Options opts, Writer writer) throws IOException {
        throw new UnsupportedOperationException(warnHint());
    }

    String warnHint();
}