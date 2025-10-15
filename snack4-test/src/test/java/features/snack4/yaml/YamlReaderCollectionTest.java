package features.snack4.yaml;

import org.noear.snack4.ONode;
import org.junit.jupiter.api.Test;
import org.noear.snack4.yaml.YamlReader;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * YamlReader 集合类型测试
 */
class YamlReaderCollectionTest {

    @Test
    void testReadFlowSequence() throws IOException {
        String yaml = "[apple, banana, cherry]";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isArray());

        List<ONode> array = node.getArray();
        assertEquals(3, array.size());
        assertEquals("apple", array.get(0).getString());
        assertEquals("banana", array.get(1).getString());
        assertEquals("cherry", array.get(2).getString());
    }

    @Test
    void testReadFlowSequenceWithMixedTypes() throws IOException {
        String yaml = "[1, \"two\", 3.0, true]";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isArray());

        List<ONode> array = node.getArray();
        assertEquals(4, array.size());
        assertEquals(1, array.get(0).getInt());
        assertEquals("two", array.get(1).getString());
        assertEquals(3.0, array.get(2).getDouble(), 0.001);
        assertTrue(array.get(3).getBoolean());
    }

    @Test
    void testReadFlowMapping() throws IOException {
        String yaml = "{name: John, age: 30, active: true}";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isObject());

        Map<String, ONode> obj = node.getObject();
        assertEquals(3, obj.size());
        assertEquals("John", obj.get("name").getString());
        assertEquals(30, obj.get("age").getInt());
        assertTrue(obj.get("active").getBoolean());
    }

    @Test
    void testReadNestedFlowStructures() throws IOException {
        String yaml = "{users: [john, jane], settings: {theme: dark, notifications: true}}";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isObject());

        Map<String, ONode> obj = node.getObject();
        assertTrue(obj.get("users").isArray());
        assertTrue(obj.get("settings").isObject());

        List<ONode> users = obj.get("users").getArray();
        assertEquals(2, users.size());
        assertEquals("john", users.get(0).getString());
        assertEquals("jane", users.get(1).getString());

        Map<String, ONode> settings = obj.get("settings").getObject();
        assertEquals("dark", settings.get("theme").getString());
        assertTrue(settings.get("notifications").getBoolean());
    }

    @Test
    void testReadBlockSequence() throws IOException {
        String yaml = "- apple\n- banana\n- cherry";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isArray());

        List<ONode> array = node.getArray();
        assertEquals(3, array.size());
        assertEquals("apple", array.get(0).getString());
        assertEquals("banana", array.get(1).getString());
        assertEquals("cherry", array.get(2).getString());
    }

    @Test
    void testReadBlockMapping() throws IOException {
        String yaml = "name: John Doe\nage: 30\nactive: true";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isObject());

        Map<String, ONode> obj = node.getObject();
        assertEquals(3, obj.size());
        assertEquals("John Doe", obj.get("name").getString());
        assertEquals(30, obj.get("age").getInt());
        assertTrue(obj.get("active").getBoolean());
    }

    @Test
    void testReadNestedBlockStructures() throws IOException {
        String yaml = "users:\n  - john\n  - jane\nsettings:\n  theme: dark\n  notifications: true";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isObject());

        Map<String, ONode> obj = node.getObject();
        assertTrue(obj.get("users").isArray());
        assertTrue(obj.get("settings").isObject());

        List<ONode> users = obj.get("users").getArray();
        assertEquals(2, users.size());
        assertEquals("john", users.get(0).getString());
        assertEquals("jane", users.get(1).getString());

        Map<String, ONode> settings = obj.get("settings").getObject();
        assertEquals("dark", settings.get("theme").getString());
        assertTrue(settings.get("notifications").getBoolean());
    }

    @Test
    void testReadComplexNestedStructure() throws IOException {
        String yaml = "people:\n  - name: John\n    age: 30\n    hobbies: [reading, swimming]\n  - name: Jane\n    age: 25\n    hobbies: [painting, hiking]\nmetadata:\n  count: 2\n  timestamp: 2024-01-01";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isObject());

        Map<String, ONode> obj = node.getObject();
        assertTrue(obj.get("people").isArray());
        assertTrue(obj.get("metadata").isObject());

        List<ONode> people = obj.get("people").getArray();
        assertEquals(2, people.size());

        Map<String, ONode> john = people.get(0).getObject();
        assertEquals("John", john.get("name").getString());
        assertEquals(30, john.get("age").getInt());
        assertTrue(john.get("hobbies").isArray());

        Map<String, ONode> metadata = obj.get("metadata").getObject();
        assertEquals(2, metadata.get("count").getInt());
        assertEquals("2024-01-01", metadata.get("timestamp").getString());
    }

    @Test
    void testReadEmptyArray() throws IOException {
        String yaml = "[]";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isArray());
        assertTrue(node.getArray().isEmpty());
    }

    @Test
    void testReadEmptyObject() throws IOException {
        String yaml = "{}";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isObject());
        assertTrue(node.getObject().isEmpty());
    }
}