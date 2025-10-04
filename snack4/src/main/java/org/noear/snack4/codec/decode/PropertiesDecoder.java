package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.NodeDecoder;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author noear 2025/10/3 created
 */
public class PropertiesDecoder implements NodeDecoder<Properties> {
    @Override
    public Properties decode(Options opts, ONodeAttr attr, ONode node, Class<?> clazz) {
        Properties properties = new Properties();
        flattenNodeToProperties(node, properties, "");
        return properties;
    }


    // 将嵌套的ONode扁平化为Properties
    private void flattenNodeToProperties(ONode node, Properties properties, String prefix) {
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