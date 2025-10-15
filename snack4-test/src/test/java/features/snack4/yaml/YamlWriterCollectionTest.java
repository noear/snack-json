package features.snack4.yaml;


import org.noear.snack4.ONode;
import org.noear.snack4.core.Options;
import org.junit.jupiter.api.Test;
import org.noear.snack4.yaml.YamlWriter;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * YamlWriter 集合类型测试
 */
class YamlWriterCollectionTest {

    @Test
    void testWriteFlowArray() throws IOException {
        ONode node = new ONode();
        node.add("apple").add("banana").add("cherry");

        String result = YamlWriter.write(node, Options.DEF_OPTIONS);
        assertTrue(result.contains("[apple, banana, cherry]") ||
                result.contains("- apple") && result.contains("- banana") && result.contains("- cherry"));
    }

    @Test
    void testWriteBlockArray() throws IOException {
        ONode node = new ONode();
        node.add("apple").add("banana").add("cherry");

        String result = YamlWriter.write(node, Options.DEF_OPTIONS);
        // 应该使用块样式或流样式
        assertTrue(result.contains("apple") && result.contains("banana") && result.contains("cherry"));
    }

    @Test
    void testWriteFlowObject() throws IOException {
        ONode node = new ONode();
        node.set("name", "John").set("age", 30).set("active", true);

        String result = YamlWriter.write(node, Options.DEF_OPTIONS);
        assertTrue(result.contains("name:") && result.contains("John") &&
                result.contains("age:") && result.contains("30") &&
                result.contains("active:") && result.contains("true"));
    }

    @Test
    void testWriteBlockObject() throws IOException {
        ONode node = new ONode();
        node.set("name", "John").set("age", 30).set("active", true);

        String result = YamlWriter.write(node, Options.DEF_OPTIONS);
        String[] lines = result.split("\n");
        boolean hasName = false, hasAge = false, hasActive = false;
        for (String line : lines) {
            if (line.contains("name: John")) hasName = true;
            if (line.contains("age: 30")) hasAge = true;
            if (line.contains("active: true")) hasActive = true;
        }
        assertTrue(hasName && hasAge && hasActive);
    }

    @Test
    void testWriteNestedStructures() throws IOException {
        ONode node = new ONode();
        ONode users = node.getOrNew("users").asArray();
        users.add("john").add("jane");

        ONode settings = node.getOrNew("settings").asObject();
        settings.set("theme", "dark").set("notifications", true);

        String result = YamlWriter.write(node, Options.DEF_OPTIONS);
        assertTrue(result.contains("users:") && result.contains("settings:"));
        assertTrue(result.contains("john") && result.contains("jane"));
        assertTrue(result.contains("theme: dark") && result.contains("notifications: true"));
    }

    @Test
    void testWriteComplexNestedStructure() throws IOException {
        ONode node = new ONode();

        ONode people = node.getOrNew("people").asArray();

        ONode person1 = new ONode();
        person1.set("name", "John").set("age", 30);
        ONode hobbies1 = person1.getOrNew("hobbies").asArray();
        hobbies1.add("reading").add("swimming");
        people.add(person1);

        ONode person2 = new ONode();
        person2.set("name", "Jane").set("age", 25);
        ONode hobbies2 = person2.getOrNew("hobbies").asArray();
        hobbies2.add("painting").add("hiking");
        people.add(person2);

        ONode metadata = node.getOrNew("metadata").asObject();
        metadata.set("count", 2).set("timestamp", "2024-01-01");

        String result = YamlWriter.write(node, Options.DEF_OPTIONS);
        assertTrue(result.contains("people:"));
        assertTrue(result.contains("name: John") && result.contains("name: Jane"));
        assertTrue(result.contains("age: 30") && result.contains("age: 25"));
        assertTrue(result.contains("hobbies:") && (result.contains("reading") || result.contains("[reading, swimming]")));
        assertTrue(result.contains("metadata:"));
        assertTrue(result.contains("count: 2") && result.contains("timestamp: \"2024-01-01\""));
    }

    @Test
    void testWriteEmptyArray() throws IOException {
        ONode node = new ONode();
        node.asArray();

        String result = YamlWriter.write(node, Options.DEF_OPTIONS);
        assertTrue(result.contains("[]"));
    }

    @Test
    void testWriteEmptyObject() throws IOException {
        ONode node = new ONode();
        node.asObject();

        String result = YamlWriter.write(node, Options.DEF_OPTIONS);
        assertTrue(result.contains("{}"));
    }

    @Test
    void testWriteArrayWithNull() throws IOException {
        ONode node = new ONode();
        node.add("value1").add(null).add("value2");

        String result = YamlWriter.write(node, Options.DEF_OPTIONS);
        assertTrue(result.contains("value1") && result.contains("null") && result.contains("value2"));
    }

    @Test
    void testWriteObjectWithNull() throws IOException {
        ONode node = new ONode();
        node.set("key1", "value1").set("key2", null).set("key3", "value3");

        String result = YamlWriter.write(node, Options.DEF_OPTIONS);
        assertTrue(result.contains("key1: value1") && result.contains("key3: value3"));
        // null 值可能被过滤或写入为 null
    }
}