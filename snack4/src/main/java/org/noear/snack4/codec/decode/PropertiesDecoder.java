package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author noear 2025/10/3 created
 */
public class PropertiesDecoder implements ObjectDecoder<Properties> {
    @Override
    public Properties decode(DecodeContext<Properties> ctx, ONode node) {
        Properties properties = ctx.getTarget();
        if(properties == null) {
            properties = new Properties();
        }

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