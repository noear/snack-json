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
import org.noear.snack4.codec.util.ClassUtil;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public class _PropertiesPatternDecoder implements ObjectPatternDecoder<Properties> {
    @Override
    public boolean canDecode(Class<?> clazz) {
        return Properties.class.isAssignableFrom(clazz);
    }

    @Override
    public Properties decode(DecodeContext<Properties> ctx, ONode node) {
        Properties p = ctx.getTarget();
        if (p == null) {
            p = (Properties) ClassUtil.newInstance(ctx.getType());
        }

        flattenNodeToProperties(node, p, "");
        return p;
    }


    // 将嵌套的ONode扁平化为Properties
    static void flattenNodeToProperties(ONode node, Properties properties, String prefix) {
        if (node.isObject()) {
            for (Map.Entry<String, ONode> entry : node.getObject().entrySet()) {
                String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
                flattenNodeToProperties(entry.getValue(), properties, key);
            }
        } else if (node.isArray()) {
            List<ONode> array = node.getArray();
            for (int i = 0; i < array.size(); i++) {
                String key = prefix + "[" + i + "]";
                flattenNodeToProperties(array.get(i), properties, key);
            }
        } else {
            properties.setProperty(prefix, node.getString());
        }
    }
}