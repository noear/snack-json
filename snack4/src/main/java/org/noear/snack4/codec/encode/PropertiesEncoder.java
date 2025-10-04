package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.NodeEncoder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Properties;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class PropertiesEncoder implements NodeEncoder<Properties> {
    @Override
    public ONode encode(Options opts, ONodeAttr attr, Properties properties) {
        ONode rootNode = new ONode(new LinkedHashMap<>());
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            setNestedValue(rootNode, key, value);
        }
        return rootNode;
    }

    // 设置嵌套值
    private static void setNestedValue(ONode node, String key, String value) {
        String[] parts = key.split("\\.");
        ONode current = node;
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i == parts.length - 1) {
                current.set(part, new ONode(value));
            } else {
                if (part.endsWith("]")) {
                    // 处理数组
                    String arrayName = part.substring(0, part.indexOf('['));
                    int index = Integer.parseInt(part.substring(part.indexOf('[') + 1, part.indexOf(']')));
                    ONode arrayNode = current.get(arrayName);
                    if (arrayNode == null || arrayNode.isNull()) {
                        arrayNode = new ONode(new ArrayList<>());
                        current.set(arrayName, arrayNode);
                    }
                    while (arrayNode.getArray().size() <= index) {
                        arrayNode.add(new ONode(new LinkedHashMap<>()));
                    }
                    current = arrayNode.get(index);
                } else {
                    ONode nextNode = current.get(part);
                    if (nextNode == null || nextNode.isNull()) {
                        nextNode = new ONode(new LinkedHashMap<>());
                        current.set(part, nextNode);
                    }
                    current = nextNode;
                }
            }
        }
    }
}
