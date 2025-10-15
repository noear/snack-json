package features.snack4.yaml;

import org.noear.snack4.ONode;
import org.noear.snack4.node.Options;
import org.junit.jupiter.api.Test;
import org.noear.snack4.yaml.YamlReader;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

/**
 * YamlReader 基础功能测试
 */
class YamlReaderBasicTest {

    @Test
    void testReadNull() throws IOException {
        String yaml = "null";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isNull());
    }

    @Test
    void testReadBoolean() throws IOException {
        // 测试各种布尔值表示
        String[] trueValues = {"true", "True", "TRUE", "yes", "Yes", "YES", "on", "On", "ON"};
        String[] falseValues = {"false", "False", "FALSE", "no", "No", "NO", "off", "Off", "OFF"};

        for (String value : trueValues) {
            ONode node = YamlReader.read(value);
            assertTrue(node.isBoolean());
            assertTrue(node.getBoolean());
        }

        for (String value : falseValues) {
            ONode node = YamlReader.read(value);
            assertTrue(node.isBoolean());
            assertFalse(node.getBoolean());
        }
    }

    @Test
    void testReadInteger() throws IOException {
        String yaml = "123";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isNumber());
        assertEquals(123, node.getInt());
    }

    @Test
    void testReadNegativeInteger() throws IOException {
        String yaml = "-456";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isNumber());
        assertEquals(-456, node.getInt());
    }

    @Test
    void testReadFloat() throws IOException {
        String yaml = "3.14";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isNumber());
        assertEquals(3.14, node.getDouble(), 0.001);
    }

    @Test
    void testReadScientificNotation() throws IOException {
        String yaml = "1.23e+4";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isNumber());
        assertEquals(12300.0, node.getDouble(), 0.001);
    }

    @Test
    void testReadString() throws IOException {
        String yaml = "hello world";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isString());
        assertEquals("hello world", node.getString());
    }

    @Test
    void testReadQuotedString() throws IOException {
        String yaml = "\"quoted string\"";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isString());
        assertEquals("quoted string", node.getString());
    }

    @Test
    void testReadSingleQuotedString() throws IOException {
        String yaml = "'single quoted'";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isString());
        assertEquals("single quoted", node.getString());
    }

    @Test
    void testReadEmptyString() throws IOException {
        String yaml = "\"\"";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isString());
        assertEquals("", node.getString());
    }

    @Test
    void testReadSpecialValues() throws IOException {
        // 测试特殊值
        ONode infNode = YamlReader.read(".inf");
        assertTrue(infNode.isNumber());
        assertEquals(Double.POSITIVE_INFINITY, infNode.getDouble());

        ONode negInfNode = YamlReader.read("-.inf");
        assertTrue(negInfNode.isNumber());
        assertEquals(Double.NEGATIVE_INFINITY, negInfNode.getDouble());

        ONode nanNode = YamlReader.read(".nan");
        assertTrue(nanNode.isNumber());
        assertTrue(Double.isNaN(nanNode.getDouble()));
    }

    @Test
    void testReadWithReader() throws IOException {
        String yaml = "test value";
        StringReader reader = new StringReader(yaml);
        ONode node = YamlReader.read(reader);
        assertTrue(node.isString());
        assertEquals("test value", node.getString());
    }

    @Test
    void testReadWithOptions() throws IOException {
        String yaml = "key: value";
        Options opts = Options.of();
        ONode node = YamlReader.read(yaml, opts);
        assertTrue(node.isObject());
        assertEquals("value", node.get("key").getString());
    }
}