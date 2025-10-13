package features.snack4.codec;

import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author noear 2025/10/9 created
 *
 */
public class NumberTypeTest {

    @Test
    public void case1(){
        Options options = Options.of().addFeature(Feature.Write_BigNumbersAsString);

        Map<String, Object> data = new HashMap<>();
        data.put("a", 1);
        data.put("b", 2L);
        data.put("c", 3F);
        data.put("d", 4D);

        //序列化
        String json = ONode.serialize(data, options);
        System.out.println(json); //{"a":1,"b":"2","c":3.0,"d":"4.0"} //b 和 d 变成字符串了
        assert json.equals("{\"a\":1,\"b\":\"2\",\"c\":3.0,\"d\":\"4.0\"}");

        json = ONode.serialize(data, Feature.Write_NumberTypeSuffix);
        System.out.println(json); //{"a":1,"b":2L,"c":3.0F,"d":4.0D} //带了数字类型（有些框架不支持）
        assert json.equals("{\"a\":1,\"b\":2L,\"c\":3.0F,\"d\":4.0D}");

        //带数字类型符号的，可以还原数字类型
        Map map = ONode.deserialize(json, Map.class);
        assert map.get("b") instanceof Long;
    }

    @Test
    public void case2(){
        Options options = Options.of().addFeature(Feature.Write_LongAsString);

        Map<String, Object> data = new HashMap<>();
        data.put("a", 1);
        data.put("b", 2L);
        data.put("c", 3F);
        data.put("d", 4D);

        //序列化
        String json = ONode.serialize(data, options);
        System.out.println(json); //{"a":1,"b":"2","c":3.0,"d":4.0} //b  变成字符串了
        assert json.equals("{\"a\":1,\"b\":\"2\",\"c\":3.0,\"d\":4.0}");
    }
}
