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
package org.noear.snack4.codec.create;

import org.noear.snack4.ONode;
import org.noear.snack4.node.Options;
import org.noear.snack4.codec.ObjectPatternCreator;

/**
 *
 * @author noear 2025/10/8 created
 * @since 4.0
 */
public class _ThrowablePatternCreator implements ObjectPatternCreator<Throwable> {
    @Override
    public boolean calCreate(Class<?> clazz) {
        return Throwable.class.isAssignableFrom(clazz);
    }

    @Override
    public Throwable create(Options opts, ONode node, Class<?> clazz) {
        String message = node.get("message").getString();

        try {
            if (message == null) {
                return (Throwable) clazz.getDeclaredConstructor().newInstance();
            } else {
                return (Throwable) clazz.getDeclaredConstructor(String.class).newInstance(message);
            }
        } catch (Exception ex) {
            return null;
        }
    }
}