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
import org.noear.snack4.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public class _PropertiesPatternEncoder implements ObjectPatternEncoder<Properties> {
    static final Logger log = LoggerFactory.getLogger(_PropertiesPatternEncoder.class);

    @Override
    public boolean canEncode(Object value) {
        return value instanceof Properties;
    }

    @Override
    public ONode encode(EncodeContext ctx, Properties props, ONode target) {
        if (props.size() == 0) {
            return target;
        }

        //对key排序，确保数组有序
        List<String> keyVector = new ArrayList<>();
        props.keySet().forEach(k -> {
            if (k instanceof String) {
                keyVector.add((String) k);
            }
        });
        Collections.sort(keyVector);

        //确定类型
        if (keyVector.get(0).startsWith("[")) {
            target.asArray();
        } else {
            target.asObject();
        }

        for (String key : keyVector) {
            String val = props.getProperty(key);

            try {
                setNestedValue(target, key, val);
            } catch (Exception e) {
                log.warn("Failed to encode property '{}'. The value: '{}'", key, val, e);
            }
        }

        return target;
    }


    public static void setNestedValue(ONode target, String key, String val) {
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
        ONode n1 = target;

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

        n1.setValue(val);
    }
}