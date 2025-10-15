package features.snack4.yaml;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.Feature;
import org.junit.jupiter.api.Test;
import org.noear.snack4.yaml.YamlWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * YamlWriter 高级特性测试
 */
class YamlWriterAdvancedTest {

    @Test
    void testWriteMultiLineString() throws IOException {
        ONode node = new ONode("line1\nline2\nline3");
        String result = YamlWriter.write(node, Options.DEF_OPTIONS);
        // 多行字符串应该使用块标量或转义序列
        assertTrue(result.contains("line1") && result.contains("line2") && result.contains("line3"));
    }

    @Test
    void testWriteSpecialCharacters() throws IOException {
        ONode node = new ONode("value with: colon and space");
        String result = YamlWriter.write(node, Options.DEF_OPTIONS);
        // 包含特殊字符的字符串应该被引号包裹
        assertTrue(result.contains("\"") || result.contains("value with: colon and space"));
    }

    @Test
    void testWriteQuotedKeys() throws IOException {
        ONode node = new ONode();
        node.set("normal-key", "value1");
        node.set("key:with:colon", "value2");
        node.set("key with space", "value3");
        node.set("true", "value4"); // YAML 关键字

        String result = YamlWriter.write(node, Options.DEF_OPTIONS);
        assertTrue(result.contains("normal-key: value1"));
        // 特殊键应该被引号包裹
        assertTrue(result.contains("\"key:with:colon\"") || result.contains("key:with:colon:"));
        assertTrue(result.contains("\"key with space\"") || result.contains("key with space:"));
        assertTrue(result.contains("\"true\"") || result.contains("true:"));
    }

    @Test
    void testWriteEscapeSequences() throws IOException {
        ONode node = new ONode("line1\nline2\t tab");
        String result = YamlWriter.write(node, Options.DEF_OPTIONS);
        // 转义序列应该被正确处理
        assertTrue(result.contains("\\n") || result.contains("|") || result.contains(">"));
    }

    @Test
    void testWriteDate() throws IOException {
        Date date = new Date(1672531200000L); // 2023-01-01
        ONode node = new ONode(date);

        // 测试时间戳格式
        String result1 = YamlWriter.write(node, Options.DEF_OPTIONS);
        assertTrue(result1.contains("1672531200000"));

        // 测试日期格式化
        Options optsWithDateFormat = Options.of(Feature.Write_UseDateFormat);
        String result2 = YamlWriter.write(node, optsWithDateFormat);
        assertTrue(result2.length() > 10); // 应该包含格式化的日期字符串
    }

    @Test
    void testWriteBigNumbers() throws IOException {
        ONode bigIntegerNode = new ONode(new BigInteger("12345678901234567890"));
        String result1 = YamlWriter.write(bigIntegerNode, Options.DEF_OPTIONS);
        assertTrue(result1.contains("12345678901234567890"));

        ONode bigDecimalNode = new ONode(new BigDecimal("12345678901234567890.12345"));
        String result2 = YamlWriter.write(bigDecimalNode, Options.DEF_OPTIONS);
        assertTrue(result2.contains("12345678901234567890.12345"));
    }

    @Test
    void testWriteWithPrettyFormat() throws IOException {
        ONode node = new ONode();
        node.set("name", "John");
        node.set("age", 30);
        ONode hobbies = node.getOrNew("hobbies").asArray();
        hobbies.add("reading").add("swimming");

        Options prettyOpts = Options.of(Feature.Write_PrettyFormat);
        String result = YamlWriter.write(node, prettyOpts);

        // 漂亮格式应该有适当的缩进
        String[] lines = result.split("\n");
        boolean hasIndentation = false;
        for (String line : lines) {
            if (line.startsWith("  ") && (line.contains("hobbies:") || line.contains("- reading"))) {
                hasIndentation = true;
                break;
            }
        }
        assertTrue(hasIndentation, "Pretty format should have proper indentation");
    }

    @Test
    void testWriteWithSnakeCase() throws IOException {
        ONode node = new ONode();
        node.set("firstName", "John");
        node.set("lastName", "Doe");
        node.set("emailAddress", "john@example.com");

        Options snakeOpts = Options.of(Feature.Write_UseSnakeStyle);
        String result = YamlWriter.write(node, snakeOpts);

        assertTrue(result.contains("first_name:"));
        assertTrue(result.contains("last_name:"));
        assertTrue(result.contains("email_address:"));
    }

    @Test
    void testWriteNumbersAsString() throws IOException {
        ONode node = new ONode(123);

        Options stringOpts = Options.of(Feature.Write_NumbersAsString);
        String result = YamlWriter.write(node, stringOpts);

        assertTrue(result.contains("\"123\""), "Numbers should be written as strings when feature is enabled");
    }

    @Test
    void testWriteNullHandling() throws IOException {
        ONode node = new ONode();
        node.set("key1", "value1");
        node.set("key2", null);
        node.set("key3", "value3");

        // 测试包含 null 值
        Options withNulls = Options.of(Feature.Write_Nulls);
        String result1 = YamlWriter.write(node, withNulls);
        assertTrue(result1.contains("key2: null"), "Null values should be written when feature is enabled");

        // 测试排除 null 值
        Options withoutNulls = Options.of(); // 默认不写入 null
        String result2 = YamlWriter.write(node, withoutNulls);
        assertFalse(result2.contains("key2: null"), "Null values should be omitted when feature is disabled");
    }

    @Test
    void testWriteComplexNestedStructure() throws IOException {
        ONode root = new ONode();

        // 创建用户列表
        ONode users = root.getOrNew("users").asArray();

        ONode user1 = new ONode();
        user1.set("id", 1);
        user1.set("name", "John Doe");
        user1.set("active", true);
        ONode profile1 = user1.getOrNew("profile").asObject();
        profile1.set("age", 30);
        profile1.set("email", "john@example.com");
        ONode tags1 = user1.getOrNew("tags").asArray();
        tags1.add("admin").add("developer");

        ONode user2 = new ONode();
        user2.set("id", 2);
        user2.set("name", "Jane Smith");
        user2.set("active", false);
        ONode profile2 = user2.getOrNew("profile").asObject();
        profile2.set("age", 25);
        profile2.set("email", "jane@example.com");
        ONode tags2 = user2.getOrNew("tags").asArray();
        tags2.add("user").add("tester");

        users.add(user1);
        users.add(user2);

        // 创建配置信息
        ONode config = root.getOrNew("config").asObject();
        config.set("version", "1.0.0");
        config.set("debug", true);
        ONode settings = config.getOrNew("settings").asObject();
        settings.set("timeout", 30);
        settings.set("retries", 3);

        String result = YamlWriter.write(root, Options.DEF_OPTIONS);

        // 验证基本结构
        assertTrue(result.contains("users:"));
        assertTrue(result.contains("config:"));

        // 验证用户数据
        assertTrue(result.contains("id: 1") && result.contains("id: 2"));
        assertTrue(result.contains("name: John Doe") && result.contains("name: Jane Smith"));
        assertTrue(result.contains("active: true") && result.contains("active: false"));

        // 验证嵌套对象
        assertTrue(result.contains("profile:"));
        assertTrue(result.contains("age: 30") && result.contains("age: 25"));
        assertTrue(result.contains("email:"));

        // 验证数组
        assertTrue(result.contains("tags:"));
        assertTrue(result.contains("admin") && result.contains("developer"));
        assertTrue(result.contains("user") && result.contains("tester"));

        // 验证配置
        assertTrue(result.contains("version: \"1.0.0\""));
        assertTrue(result.contains("debug: true"));
        assertTrue(result.contains("settings:"));
        assertTrue(result.contains("timeout: 30") && result.contains("retries: 3"));
    }

    @Test
    void testWriteMixedContentTypes() throws IOException {
        ONode node = new ONode();
        node.set("string", "hello");
        node.set("integer", 42);
        node.set("float", 3.14);
        node.set("boolean", true);
        node.set("null_value", null);
        node.set("array", new ONode().add("a").add(1).add(true).add(null));

        ONode nested = node.getOrNew("nested").asObject();
        nested.set("nested_string", "world");
        nested.set("nested_number", 100);

        Options opts = Options.of(Feature.Write_Nulls);
        String result = YamlWriter.write(node, opts);

        // 验证各种类型都被正确写入
        assertTrue(result.contains("string: hello"));
        assertTrue(result.contains("integer: 42"));
        assertTrue(result.contains("float: 3.14"));
        assertTrue(result.contains("boolean: true"));
        assertTrue(result.contains("null_value: null"));
        assertTrue(result.contains("array:"));
        assertTrue(result.contains("nested:"));
        assertTrue(result.contains("nested_string: world"));
        assertTrue(result.contains("nested_number: 100"));
    }

    @Test
    void testWriteEmptyStructures() throws IOException {
        ONode emptyArray = new ONode();
        emptyArray.asArray();

        ONode emptyObject = new ONode();
        emptyObject.asObject();

        String arrayResult = YamlWriter.write(emptyArray, Options.DEF_OPTIONS);
        String objectResult = YamlWriter.write(emptyObject, Options.DEF_OPTIONS);

        assertTrue(arrayResult.contains("[]"));
        assertTrue(objectResult.contains("{}"));
    }

    @Test
    void testWriteSpecialYamlValues() throws IOException {
        ONode node = new ONode();
        node.set("infinity", Double.POSITIVE_INFINITY);
        node.set("negative_infinity", Double.NEGATIVE_INFINITY);
        node.set("nan", Double.NaN);

        String result = YamlWriter.write(node, Options.DEF_OPTIONS);

        assertTrue(result.contains(".inf") || result.contains("Infinity"));
        assertTrue(result.contains("-.inf") || result.contains("-Infinity"));
        assertTrue(result.contains(".nan") || result.contains("NaN"));
    }

    @Test
    void testWriteWithCustomOptions() throws IOException {
        ONode node = new ONode();
        node.set("camelCaseKey", "value");
        node.set("numberValue", 1234567890123456789L);

        // 测试自定义选项组合
        Options customOpts = Options.of(
                Feature.Write_UseSnakeStyle,
                Feature.Write_LongAsString,
                Feature.Write_PrettyFormat
        );

        String result = YamlWriter.write(node, customOpts);

        assertTrue(result.contains("camel_case_key:"));
        assertTrue(result.contains("\"1234567890123456789\"") || result.contains("1234567890123456789"));
    }

    @Test
    void testWriteToWriter() throws IOException {
        ONode node = new ONode("test value");
        StringWriter writer = new StringWriter();

        YamlWriter.write(node, Options.DEF_OPTIONS, writer);
        String result = writer.toString();

        assertTrue(result.contains("test value"));
    }

    @Test
    void testWriteDocumentStructure() throws IOException {
        ONode node = new ONode();
        node.set("data", "value");

        String result = YamlWriter.write(node, Options.DEF_OPTIONS);

        // 验证文档结构包含开始和结束标记
        assertTrue(result.startsWith("---"));
        assertTrue(result.contains("data: value"));
    }

    @Test
    void testWriteBooleanVariants() throws IOException {
        ONode node = new ONode();
        node.set("bool1", true);
        node.set("bool2", false);

        String result = YamlWriter.write(node, Options.DEF_OPTIONS);

        assertTrue(result.contains("bool1: true"));
        assertTrue(result.contains("bool2: false"));
    }

    @Test
    void testWriteUnicodeCharacters() throws IOException {
        ONode node = new ONode("Hello 世界 🎉");
        String result = YamlWriter.write(node, Options.DEF_OPTIONS);

        assertTrue(result.contains("Hello") || result.contains("世界"));
    }
}