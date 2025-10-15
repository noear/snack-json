package features.snack4.yaml;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.junit.jupiter.api.Test;
import org.noear.snack4.yaml.YamlWriter;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * YamlWriter 基础功能测试
 */
class YamlWriterBasicTest {

    @Test
    void testWriteNull() throws IOException {
        ONode node = new ONode(null);
        String result = YamlWriter.write(node, Options.DEF_OPTIONS);
        assertEquals("---\nnull\n...", result.trim());
    }

    @Test
    void testWriteBoolean() throws IOException {
        ONode trueNode = new ONode(true);
        String trueResult = YamlWriter.write(trueNode, Options.DEF_OPTIONS);
        assertTrue(trueResult.contains("true"));

        ONode falseNode = new ONode(false);
        String falseResult = YamlWriter.write(falseNode, Options.DEF_OPTIONS);
        assertTrue(falseResult.contains("false"));
    }

    @Test
    void testWriteInteger() throws IOException {
        ONode node = new ONode(123);
        String result = YamlWriter.write(node, Options.DEF_OPTIONS);
        assertTrue(result.contains("123"));
    }

    @Test
    void testWriteFloat() throws IOException {
        ONode node = new ONode(3.14);
        String result = YamlWriter.write(node, Options.DEF_OPTIONS);
        assertTrue(result.contains("3.14"));
    }

    @Test
    void testWriteString() throws IOException {
        ONode node = new ONode("hello world");
        String result = YamlWriter.write(node, Options.DEF_OPTIONS);
        assertTrue(result.contains("hello world"));
    }

    @Test
    void testWriteEmptyString() throws IOException {
        ONode node = new ONode("");
        String result = YamlWriter.write(node, Options.DEF_OPTIONS);
        assertTrue(result.contains("\"\""));
    }

    @Test
    void testWriteSpecialStrings() throws IOException {
        // 测试需要引号的字符串
        ONode node1 = new ONode("true"); // YAML 关键字
        String result1 = YamlWriter.write(node1, Options.DEF_OPTIONS);
        assertTrue(result1.contains("\"true\""));

        ONode node2 = new ONode("123"); // 数字字符串
        String result2 = YamlWriter.write(node2, Options.DEF_OPTIONS);
        assertTrue(result2.contains("\"123\""));

        ONode node3 = new ONode("key:value"); // 包含特殊字符
        String result3 = YamlWriter.write(node3, Options.DEF_OPTIONS);
        assertTrue(result3.contains("\"key:value\""));
    }

    @Test
    void testWriteWithStringWriter() throws IOException {
        ONode node = new ONode("test");
        StringWriter writer = new StringWriter();
        YamlWriter.write(node, Options.DEF_OPTIONS, writer);
        String result = writer.toString();
        assertTrue(result.contains("test"));
    }

    @Test
    void testWriteWithOptions() throws IOException {
        ONode node = new ONode("test");
        Options opts = Options.of();
        String result = YamlWriter.write(node, opts);
        assertTrue(result.contains("test"));
    }
}