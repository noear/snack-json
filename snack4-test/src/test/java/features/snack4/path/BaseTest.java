package features.path;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.codec.ObjectEncoder;
import org.noear.snack4.json.JsonReader;
import org.noear.snack4.jsonpath.JsonPath;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author noear 2025/3/15 created
 */
public class BaseTest {
    @Test
    public void case1() throws Exception {
        String json = "{"
                + "\"name\": \"Alice\","
                + "\"age\": 28,"
                + "\"scores\": [95.5, 89.0, 92.3],"
                + "\"metadata\": {"
                + "  \"uid\": \"\\u0041\\u0042\\u0043\","
                + "  \"active\": true"
                + "}"
                + "}";

        ONode node = JsonReader.read(json);

        // 验证解析结果
        System.out.println(node.get("name").getString()); // Alice
        System.out.println(node.get("age").getInt()); // 28
        System.out.println(node.get("scores").get(0).getDouble()); // 95.5
        System.out.println(node.get("metadata").get("uid").getString()); // ABC
        System.out.println(node.get("metadata").get("active").getBoolean()); // true
    }


    @Test
    public void case3() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("order.item[0].user", "1");

        ONode node = ObjectEncoder.serialize(properties);
        System.out.println(node.toJson()); // 输出: {"order":{"item":[{"user":"1"}]}}
        assert "{\"order\":{\"item\":[{\"user\":\"1\"}]}}".equals(node.toJson());

        Properties deserializedProperties = ObjectDecoder.deserialize(node, Properties.class);
        System.out.println(deserializedProperties.getProperty("order.item[0].user")); // 输出: 1
        assert "1".equals(deserializedProperties.getProperty("order.item[0].user"));
    }

    @Test
    public void case4() {
        ONode root = ONode.load("{}");
        JsonPath.delete(root, "$.store.book[0]");
        ONode result = JsonPath.select(root, "$.store.book[0]");
        assertTrue(result.isNull());
    }

    @Test
    public void case5() {
        ONode root = ONode.load("{}");
        JsonPath.create(root, "$.store.newNode");
        ONode result = JsonPath.select(root, "$.store.newNode");
        assertNotNull(result);
        assertTrue(result.isNull());
    }
}
