package org.noear.snack4.codec;

import org.noear.snack4.node.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.node.Options;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author noear 2025/10/9 created
 *
 */
public class FeatureDemo {
    public static void main(String[] args) {
        Options options = Options.of().addFeatures(Feature.Write_BigNumbersAsString);

        Map<String, Object> data = new HashMap<>();
        data.put("a", 1);
        data.put("b", 2L);
        data.put("c", 3F);
        data.put("d", 4D);

        //序列化
        String json = ONode.ofBean(data, options).toJson();
        System.out.println(json); //{"a":1,"b":"2","c":3.0,"d":"4.0"} //b 和 d 变成字符串了

        json = ONode.ofBean(data, Feature.Write_NumberTypeSuffix).toJson();
        System.out.println(json); //{"a":1,"b":2L,"c":3.0F,"d":4.0D} //带了数字类型（有些框架不支持）

        //带数字类型符号的，可以还原数字类型
        Map map = ONode.ofJson(json).toBean(Map.class);
        assert map.get("b") instanceof Long;
    }
}