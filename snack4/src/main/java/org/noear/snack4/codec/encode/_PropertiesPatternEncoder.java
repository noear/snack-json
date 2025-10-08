package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectPatternEncoder;
import org.noear.snack4.util.Asserts;

import java.util.*;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class _PropertiesPatternEncoder implements ObjectPatternEncoder<Properties> {
    @Override
    public boolean canEncode(Object value) {
        return value instanceof Properties;
    }

    @Override
    public ONode encode(EncodeContext ctx, Properties properties) {
        ONode rootNode = new ONode(ctx.getOpts());

        //对key排序，确保数组有序
        List<String> keyVector = new ArrayList<>();
        properties.keySet().forEach(k -> {
            if (k instanceof String) {
                keyVector.add((String) k);
            }
        });
        Collections.sort(keyVector);

        //确定类型
        if (keyVector.get(0).startsWith("[")) {
            rootNode.asArray();
        } else {
            rootNode.asObject();
        }

        for (String key : keyVector) {
            String val = properties.getProperty(key);

            setNestedValue(rootNode, key, val);
        }


        return rootNode;
    }

    // 设置嵌套值
    private static void setNestedValue(ONode node, String key, String value) {
        /**
         *  ("title", "test");
         *  ("debug", "true");
         *  ("user.id", "1");
         *  ("user.name", "noear");
         *  ("server.urls[0]", "http://x.x.x");
         *  ("server.urls[1]", "http://y.y.y");
         *  ("user.orders[0].items[0].name", "手机");
         *  ("type[]", "a");
         *  ("type[]", "b");
         * */
        String[] keySegments = key.split("\\.");
        ONode n1 = node;

        for (int i = 0; i < keySegments.length; i++) {
            String p1 = keySegments[i];

            if (p1.endsWith("]")) {
                String tmp = p1.substring(p1.lastIndexOf('[') + 1, p1.length() - 1);//?=$[?]
                p1 = p1.substring(0, p1.lastIndexOf('[')); //?=?[$]

                if (tmp.length() > 0) {
                    if (Asserts.isInteger(tmp)) {
                        //[1]
                        int idx = Integer.parseInt(tmp);

                        if (p1.length() > 0) {
                            n1 = n1.getOrNew(p1).getOrNew(idx);
                        } else {
                            n1 = n1.getOrNew(idx);
                        }
                    } else {
                        if (tmp.length() > 2 && (tmp.indexOf('\'') == 0 || tmp.indexOf('"') == 0)) {
                            tmp = tmp.substring(1, tmp.length() - 1);
                        }

                        //[a]
                        if (p1.length() > 0) {
                            n1 = n1.getOrNew(p1).getOrNew(tmp);
                        } else {
                            n1 = n1.getOrNew(tmp);
                        }
                    }
                } else {
                    //[]
                    if (p1.length() > 0) {
                        n1 = n1.getOrNew(p1).addNew();
                    } else {
                        n1 = n1.addNew();
                    }
                }

            } else {
                n1 = n1.getOrNew(p1);
            }
        }

        n1.setValue(value);
    }
}
