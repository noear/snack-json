package features.snack4.yaml;

import org.noear.snack4.ONode;
import org.junit.jupiter.api.Test;
import org.noear.snack4.yaml.YamlReader;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * YamlReader 高级特性测试
 */
class YamlReaderAdvancedTest {

    @Test
    void testReadAnchorsAndAliases() throws IOException {
        String yaml = "base: &base\n  name: default\n  value: 100\nderived:\n  <<: *base\n  extra: added";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isObject());

        Map<String, ONode> obj = node.getObject();
        assertTrue(obj.get("base").isObject());
        assertTrue(obj.get("derived").isObject());

        Map<String, ONode> base = obj.get("base").getObject();
        Map<String, ONode> derived = obj.get("derived").getObject();

        assertEquals("default", base.get("name").getString());
        assertEquals(100, base.get("value").getInt());
        assertEquals("added", derived.get("extra").getString());
    }

    @Test
    void testReadTags() throws IOException {
        String yaml = "string: !!str 123\ninteger: !!int \"456\"\nfloat: !!float \"7.89\"\nboolean: !!bool \"true\"";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isObject());

        Map<String, ONode> obj = node.getObject();

        // !!str 标签
        assertTrue(obj.get("string").isString());
        assertEquals("123", obj.get("string").getString());

        // !!int 标签
        assertTrue(obj.get("integer").isNumber());
        assertEquals(456, obj.get("integer").getInt());

        // !!float 标签
        assertTrue(obj.get("float").isNumber());
        assertEquals(7.89, obj.get("float").getDouble(), 0.001);

        // !!bool 标签
        assertTrue(obj.get("boolean").isBoolean());
        assertTrue(obj.get("boolean").getBoolean());
    }

    @Test
    void testReadBlockScalars() throws IOException {
        // 字面块标量
        String yaml1 = "content: |\n  This is a\n  multi-line\n  string";
        ONode node1 = YamlReader.read(yaml1);
        assertEquals("This is a\nmulti-line\nstring\n", node1.get("content").getString());

        // 折叠块标量
        String yaml2 = "content: >\n  This is a\n  folded\n  string";
        ONode node2 = YamlReader.read(yaml2);
        assertEquals("This is a folded string\n", node2.get("content").getString());
    }

    @Test
    void testReadComplexKeys() throws IOException {
        String yaml = "? complex key\n: complex value\nnormal: normal value";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isObject());

        Map<String, ONode> obj = node.getObject();
        assertEquals("complex value", obj.get("complex key").getString());
        assertEquals("normal value", obj.get("normal").getString());
    }

    @Test
    void testReadDocumentMarkers() throws IOException {
        String yaml = "---\nname: Document 1\n---\nname: Document 2\n...";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isObject());
        assertEquals("Document 1", node.get("name").getString());
    }

    @Test
    void testReadEscapeSequences() throws IOException {
        String yaml = "normal: \"line1\\nline2\"\nunicode: \"\\u0041\"\nspecial: \"quote\\\"backslash\\\\\"";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isObject());

        Map<String, ONode> obj = node.getObject();
        assertEquals("line1\nline2", obj.get("normal").getString());
        assertEquals("A", obj.get("unicode").getString());
        assertEquals("quote\"backslash\\", obj.get("special").getString());
    }

    @Test
    void testReadComments() throws IOException {
        String yaml = "# This is a comment\nname: John  # Inline comment\nage: 30\n# Another comment\nactive: true";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isObject());

        Map<String, ONode> obj = node.getObject();
        assertEquals("John", obj.get("name").getString());
        assertEquals(30, obj.get("age").getInt());
        assertTrue(obj.get("active").getBoolean());
    }

    @Test
    void testReadMultiDocument() throws IOException {
        String yaml = "---\ndoc1: value1\n---\ndoc2: value2";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isObject());
        assertEquals("value1", node.get("doc1").getString());
    }

    @Test
    void testReadHexadecimal() throws IOException {
        String yaml = "hex: 0x1A";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isObject());
        assertEquals(26, node.get("hex").getInt());
    }

    @Test
    void testReadOctal() throws IOException {
        String yaml = "oct: 0o12";
        ONode node = YamlReader.read(yaml);
        assertTrue(node.isObject());
        assertEquals(10, node.get("oct").getInt());
    }
}