package features.snack4.yaml;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.junit.jupiter.api.Test;
import org.noear.snack4.yaml.YamlReader;
import org.noear.snack4.yaml.YamlWriter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * YAML 读写器兼容性测试（往返测试）
 */
class YamlCompatibilityTest {

    @Test
    void testRoundTripSimpleValues() throws IOException {
        testRoundTrip(new ONode("simple string"));
        testRoundTrip(new ONode(42));
        testRoundTrip(new ONode(3.14));
        testRoundTrip(new ONode(true));
        testRoundTrip(new ONode(false));
        testRoundTrip(new ONode(null));
    }

    @Test
    void testRoundTripArray() throws IOException {
        ONode original = new ONode();
        original.add("a").add(1).add(true).add(null);
        testRoundTrip(original);
    }

    @Test
    void testRoundTripObject() throws IOException {
        ONode original = new ONode();
        original.set("name", "John");
        original.set("age", 30);
        original.set("active", true);
        testRoundTrip(original);
    }

    @Test
    void testRoundTripNestedStructures() throws IOException {
        ONode original = new ONode();
        ONode users = original.getOrNew("users").asArray();

        ONode user1 = new ONode();
        user1.set("name", "John");
        user1.set("hobbies", new ONode().add("reading").add("swimming"));
        users.add(user1);

        ONode user2 = new ONode();
        user2.set("name", "Jane");
        user2.set("hobbies", new ONode().add("painting"));
        users.add(user2);

        testRoundTrip(original);
    }

    @Test
    void testRoundTripComplexStructure() throws IOException {
        ONode original = new ONode();

        // 配置信息
        original.set("app", "Test Application");
        original.set("version", "1.0.0");

        // 数据库配置
        ONode database = original.getOrNew("database").asObject();
        database.set("host", "localhost");
        database.set("port", 5432);
        database.set("credentials", new ONode().set("username", "admin").set("password", "secret"));

        // 功能开关
        ONode features = original.getOrNew("features").asObject();
        features.set("logging", true);
        features.set("cache", false);
        features.set("limits", new ONode().set("max_connections", 100).set("timeout", 30));

        // 服务列表
        ONode services = original.getOrNew("services").asArray();
        services.add(new ONode().set("name", "api").set("port", 8080));
        services.add(new ONode().set("name", "auth").set("port", 8081));

        testRoundTrip(original);
    }

    @Test
    void testRoundTripWithSpecialCharacters() throws IOException {
        ONode original = new ONode();
        original.set("normal", "value");
        original.set("with:colon", "value1");
        original.set("with space", "value2");
        original.set("with\"quote", "value3");
        original.set("with\\backslash", "value4");
        original.set("with\nnewline", "value5");

        testRoundTrip(original);
    }

    @Test
    void testRoundTripWithYamlKeywords() throws IOException {
        ONode original = new ONode();
        original.set("true", "this is true keyword");
        original.set("false", "this is false keyword");
        original.set("null", "this is null keyword");
        original.set("yes", "this is yes keyword");
        original.set("no", "this is no keyword");

        testRoundTrip(original);
    }

    @Test
    void testRoundTripWithNumbers() throws IOException {
        ONode original = new ONode();
        original.set("int", 42);
        original.set("negative", -100);
        original.set("float", 3.14159);
        original.set("scientific", 1.23e10);
        original.set("zero", 0);

        testRoundTrip(original);
    }

    @Test
    void testRoundTripEmptyStructures() throws IOException {
        testRoundTrip(new ONode().asArray()); // 空数组
        testRoundTrip(new ONode().asObject()); // 空对象
    }

    @Test
    void testRoundTripMixedArray() throws IOException {
        ONode original = new ONode();
        original.add("string")
                .add(123)
                .add(45.67)
                .add(true)
                .add(false)
                .add(null)
                .add(new ONode().set("nested", "value"))
                .add(new ONode().add("a").add("b"));

        testRoundTrip(original);
    }

    private void testRoundTrip(ONode original) throws IOException {
        // 写入 YAML
        String yaml = YamlWriter.write(original, Options.DEF_OPTIONS);
        assertNotNull(yaml);
        assertFalse(yaml.trim().isEmpty());

        // 读取 YAML
        ONode reconstructed = YamlReader.read(yaml);
        assertNotNull(reconstructed);

        // 验证数据一致性
        assertNodesEqual(original, reconstructed);
    }

    private void assertNodesEqual(ONode expected, ONode actual) {
        assertEquals(expected.nodeType(), actual.nodeType(), "Node types should match");

        switch (expected.nodeType()) {
            case Null:
            case Undefined:
                assertTrue(actual.isNull() || actual.isUndefined());
                break;
            case Boolean:
                assertEquals(expected.getBoolean(), actual.getBoolean());
                break;
            case Number:
                // 使用容差比较数字
                double expectedValue = expected.getDouble();
                double actualValue = actual.getDouble();
                assertEquals(expectedValue, actualValue, 0.0001);
                break;
            case String:
                assertEquals(expected.getString(), actual.getString());
                break;
            case Array:
                List<ONode> expectedArray = expected.getArray();
                List<ONode> actualArray = actual.getArray();
                assertEquals(expectedArray.size(), actualArray.size(), "Array sizes should match");
                for (int i = 0; i < expectedArray.size(); i++) {
                    assertNodesEqual(expectedArray.get(i), actualArray.get(i));
                }
                break;
            case Object:
                Map<String, ONode> expectedObject = expected.getObject();
                Map<String, ONode> actualObject = actual.getObject();
                assertEquals(expectedObject.size(), actualObject.size(), "Object sizes should match");
                for (Map.Entry<String, ONode> entry : expectedObject.entrySet()) {
                    String key = entry.getKey();
                    assertTrue(actualObject.containsKey(key), "Key should exist: " + key);
                    assertNodesEqual(entry.getValue(), actualObject.get(key));
                }
                break;
        }
    }
}