package features.snack4.json.reader;

import org.junit.jupiter.api.Test;
import org.noear.snack4.core.Feature;
import org.noear.snack4.json.JsonReader;
import org.noear.snack4.core.Options;
import org.noear.snack4.json.JsonParseException;

import static org.junit.jupiter.api.Assertions.*;

class JsonReaderComplexTest {

    // ========================= 复杂测试用例（30 个） =========================

    @Test
    void testParseInvalidJsonMissingClosingBrace() {
        String json = "{\"name\": \"Alice\"";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonMissingClosingBracket() {
        String json = "[1, 2, 3";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonExtraComma() {
        String json = "{\"name\": \"Alice\",}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonExtraCommaInArray() {
        String json = "[1, 2, 3,]";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonMissingKey() {
        String json = "{: \"Alice\"}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonMissingValue() {
        String json = "{\"name\":}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidNumber() {
        String json = "123abc";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidBoolean() {
        String json = "tru";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidNull() {
        String json = "nul";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidString() {
        String json = "\"Hello";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidUnicode() {
        String json = "\"\\u123\"";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidEscape() {
        String json = "\"\\x\"";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidObject() {
        String json = "{name: \"Alice\"}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json, Options.of(Feature.Read_DisableUnquotedKeys)));
    }

    @Test
    void testParseInvalidJsonInvalidArray() {
        String json = "[1, 2, 3,}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonEmptyKey() {
        String json = "{\"\": \"Alice\"}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidNestedObject() {
        String json = "{\"person\": {\"name\": \"Alice\", \"age\":}}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidNestedArray() {
        String json = "{\"scores\": [1, 2,]}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidScientificNotation() {
        String json = "1.23e";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidNegativeScientificNotation() {
        String json = "-1.23e-";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidFloat() {
        String json = "3.14.15";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidNegativeFloat() {
        String json = "-3.14.15";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidBooleanInObject() {
        String json = "{\"active\": tru}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidNullInObject() {
        String json = "{\"value\": nul}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidMixedTypesInObject() {
        String json = "{\"name\": \"Alice\", \"age\": twenty-eight}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidBooleanInArray() {
        String json = "[true, fals, true]";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidNullInArray() {
        String json = "[null, nul, null]";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidMixedTypesInArray() {
        String json = "[1, \"two\", three]";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidDeeplyNestedObject() {
        String json = "{\"a\": {\"b\": {\"c\": {\"d\": {\"e\": {\"f\": {\"g\": 42}}}}}}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidDeeplyNestedArray() {
        String json = "[[[[[[[42]]]]]]";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }
}