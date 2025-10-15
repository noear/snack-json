package features.snack4.jsonpath.RFC9535;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.JsonPath;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author noear 2025/10/11 created
 *
 */
public class FunctionTest extends AbsRFC9535 {
    // SQL/JSON Path (ISO/IEC 9075)
    // IETF JSONPath (RFC 9535) https://www.rfc-editor.org/rfc/rfc9535.html

    @Test
    public void countTest() {
        queryAssertSize(count_json, "$.inventory.books[?(@.price > 20)]", 2);
        queryAssertSize(count_json, "$.inventory.books[?(count(@.tags) == 2)]", 3);
        queryAssertSize(count_json, "$.inventory[?(count(@.*) > 3)]", 1);
        queryAssertSize(count_json, "$.inventory.books[?(count(@.tags) > 0 && @.price < 20)]", 1);
        queryAssertSize(count_json, "$..[?(count(@) == 2)]", 7);
    }

    @Test
    public void lengthTest(){
        queryAssertSize(length_json,"$.library.sections[?(length(@.books) > 2)]",1);
        queryAssertSize(length_json,"$.library.sections[?(length(@.name) < 8)]",3);
        queryAssertSize(length_json,"$.library.sections[?(length(@.books) == 0)]",1);
        queryAssertSize(length_json,"$.library.sections[?(length(@.books) > 0 && length(@.name) > 10)]",0);
        queryAssertSize(length_json,"$.library.sections[?(length(@.books[0]) > 10)]",1);
    }

    @Test
    public void matchTest(){
        queryAssertSize(match_json,"$.employees[?(match(@.id, \"emp-\\\\d{3}\"))]",2);
        queryAssertSize(match_json,"$.employees[?(match(@.id, \"(?i)emp-\\\\d{3}\"))]",3);
        queryAssertSize(match_json,"$.employees[?(search(@.email, \"example\"))]",3);
        queryAssertSize(match_json,"$.employees[?(search(@.email, \"\\\\.org$\"))]",1);
        queryAssertSize(match_json,"$.employees[?(match(@.name, \"Jane.*\"))]",1);
    }

    @Test
    public void valueTest(){
        queryAssertSize(value_json,"$.readings[?(value(@.type) == 'temperature')].value",1);
        queryAssertSize(value_json,"$.readings[?(value(@..unit) == 'hPa')].type",1);
        queryAssertSize(value_json,"$.readings[?(value(@.value) > 100)]",1);
        queryAssertSize(value_json,"$[?(value($.location.lat) > 30)].device",0);
        queryAssertSize(value_json,"$[?(value($.device) == 'weather-station-01')].readings",0);
        queryAssertSize(value_json,"$.readings[?($.location.lat > 30)]",3);
        queryAssertSize(value_json,"$.readings[?($.location.lat > 30)].type",3);
        queryAssertSize(value_json,"$.readings[?(@.value > 50 && $.location.lat < 40)]",2);

    }

    @Test
    public void debugTest() {

    }

    static String value_json = "{\n" +
            "  \"device\": \"weather-station-01\",\n" +
            "  \"readings\": [\n" +
            "    {\n" +
            "      \"type\": \"temperature\",\n" +
            "      \"value\": 22.5,\n" +
            "      \"unit\": \"Celsius\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"humidity\",\n" +
            "      \"value\": 65,\n" +
            "      \"unit\": \"%\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"pressure\",\n" +
            "      \"value\": 1012,\n" +
            "      \"unit\": \"hPa\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"location\": {\n" +
            "    \"lat\": 34.0522,\n" +
            "    \"lon\": -118.2437\n" +
            "  }\n" +
            "}";

    static String match_json = "{\n" +
            "  \"employees\": [\n" +
            "    {\n" +
            "      \"id\": \"emp-001\",\n" +
            "      \"name\": \"John Doe\",\n" +
            "      \"email\": \"john.doe@example.com\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": \"user-002\",\n" +
            "      \"name\": \"Jane Smith\",\n" +
            "      \"email\": \"jane_smith@example.org\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": \"emp-003\",\n" +
            "      \"name\": \"Peter Jones\",\n" +
            "      \"email\": \"peter.jones@work.net\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": \"EMP-004\",\n" +
            "      \"name\": \"Mary Brown\",\n" +
            "      \"email\": \"mary@example.com\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    static String length_json = "{\n" +
            "  \"library\": {\n" +
            "    \"name\": \"Central Library\",\n" +
            "    \"sections\": [\n" +
            "      {\n" +
            "        \"name\": \"Fiction\",\n" +
            "        \"books\": [\"Book A\", \"Book B\", \"Book C\"]\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"Sci-Fi\",\n" +
            "        \"books\": [\"Dune\", \"Neuromancer\"]\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"Short Stories\",\n" +
            "        \"books\": []\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"History\",\n" +
            "        \"books\": [\"A Short History of Nearly Everything\"]\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";


    static String count_json = "{\n" +
            "  \"inventory\": {\n" +
            "    \"books\": [\n" +
            "      {\n" +
            "        \"title\": \"The Lord of the Rings\",\n" +
            "        \"author\": \"J.R.R. Tolkien\",\n" +
            "        \"price\": 25.99,\n" +
            "        \"tags\": [\"fantasy\", \"adventure\"]\n" +
            "      },\n" +
            "      {\n" +
            "        \"title\": \"The Hobbit\",\n" +
            "        \"author\": \"J.R.R. Tolkien\",\n" +
            "        \"price\": 15.50\n" +
            "      },\n" +
            "      {\n" +
            "        \"title\": \"A Game of Thrones\",\n" +
            "        \"author\": \"George R.R. Martin\",\n" +
            "        \"price\": 22.00,\n" +
            "        \"tags\": [\"fantasy\", \"drama\"]\n" +
            "      },\n" +
            "      {\n" +
            "        \"title\": \"Dune\",\n" +
            "        \"author\": \"Frank Herbert\",\n" +
            "        \"price\": 18.75,\n" +
            "        \"tags\": [\"sci-fi\", \"adventure\"]\n" +
            "      }\n" +
            "    ],\n" +
            "    \"pens\": [\n" +
            "      {\"color\": \"blue\", \"price\": 1.50},\n" +
            "      {\"color\": \"red\", \"price\": 1.50}\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    private void queryAssert(String json, String expr, String expected) {
        System.out.println("------------: " + expr);
        JsonPath jsonPath = JsonPath.parse(expr);

        String actual = jsonPath.select(ofJson(json)).asNode().toJson();
        String expected2 = ofJson(expected).toJson(); //重新格式化

        assertEquals(expected2, actual);
    }

    private void queryAssertSize(String json, String expr, int expected) {
        System.out.println("------------: " + expr);

        JsonPath jsonPath = JsonPath.parse(expr);

        List<ONode> actual = jsonPath.select(ofJson(json)).getNodeList();
        System.out.println(ONode.ofBean(actual).toJson());

        if (expected != actual.size()) {
            System.out.println(json);
            assertEquals(expected, actual.size());
        }
    }
}
