package features.snack4.jsonpath.manual.func;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

public class Func_length_size_keys_Test {
    //不开启 jayway 特性
    private static final String JSON_DATA = "{" +
            "\"store\": {" +
            "\"book\": [" +
            "{\"category\": \"reference\", \"author\": \"Nigel Rees\", \"title\": \"Sayings of the Century\", \"price\": 8.95, \"ratings\": [4, 5, 4]}," +
            "{\"category\": \"fiction\", \"author\": \"Evelyn Waugh\", \"title\": \"Sword of Honour\", \"price\": 12.99, \"ratings\": [5, 5, 5]}," +
            "{\"category\": \"fiction\", \"author\": \"Herman Melville\", \"title\": \"Moby Dick\", \"isbn\": \"0-553-21311-3\", \"price\": 8.99, \"ratings\": [3, 4, 3, 5]}," +
            "{\"category\": \"fiction\", \"author\": \"J. R. R. Tolkien\", \"title\": \"The Lord of the Rings\", \"isbn\": \"0-395-19395-8\", \"price\": 22.99, \"ratings\": [5, 5, 5, 5, 5]}" +
            "]," +
            "\"bicycle\": {\"color\": \"red\", \"price\": 19.95, \"stock\": 10}," +
            "\"staff\": [" +
            "{\"name\": \"Alice\", \"age\": 30}," +
            "{\"name\": \"Bob\", \"age\": 25}," +
            "{\"name\": \"Charlie\", \"age\": 40}" +
            "]," +
            "\"inventory\": [10, 5, 20, 15]" +
            "}," +
            "\"totalPrice\": 53.92" +
            "}";

    private static DocumentContext context;
    private static ONode oNode;

    @BeforeAll
    static void setup() {
        context = JsonPath.parse(JSON_DATA);
        oNode = ONode.ofJson(JSON_DATA);
    }

    @Test
    void lengthTest() {
        compatible_num("1", "4", "$.store.book.length()");
        compatible_num("2", "4", "$.store.inventory.length()");
        compatible_num("3", "3", "$.store.staff[*].name");
        compatible_num("3", "3", "$.store.staff[*].name.length()");
        compatible_num("4", "5", "$.store.book[3].ratings.length()");
        compatible_num("5", "3", "$.store.bicycle.color.length()");
    }

    @Test
    void sizeTest() {
        compatible_num("1", "4", "$.store.book.size()");
        compatible_num("2", "4", "$.store.inventory.size()");
        compatible_num("3", "3", "$.store.staff[*].name");
        compatible_num("3", "3", "$.store.staff[*].name.size()");
        compatible_num("4", "5", "$.store.book[3].ratings.size()");
        compatible_num("5", "3", "$.store.bicycle.color.length()");
    }

    @Test
    void keysTest() {
        compatible_str("1", "[\"book\",\"bicycle\",\"staff\",\"inventory\"]", "$.store.keys()");
        compatible_str("2", "[\"color\",\"price\",\"stock\"]", "$.store.bicycle.keys()");
        compatible_str("3", "[\"category\",\"author\",\"title\",\"price\",\"ratings\"]", "$.store.book[0].keys()");
        compatible_str("4", "[\"store\",\"totalPrice\"]", "$.keys()");
        compatible_str("5", "", "$..book[?(@.isbn)]");
        compatible_str("5", "[\"category\",\"author\",\"title\",\"isbn\",\"price\",\"ratings\"]", "$..book[?(@.isbn)].keys()");
    }

    private void compatible_num(String tag, String ref, String jsonpathStr) {
        System.out.println("::::" + tag + " - " + jsonpathStr);

        ONode tmp = oNode.select(jsonpathStr);
        System.out.println(tmp.toJson());

        try {
            Object tmp2 = context.read(jsonpathStr);
            System.out.println(ONode.serialize(tmp2));

            if (tmp.toJson().equals(ONode.serialize(tmp2)) == false) {
                assert Math.abs(tmp.getDouble() - Double.parseDouble(ref)) < 0.001;
            }
        } catch (Exception e) {
            System.out.println("jayway: err");
            assert Math.abs(tmp.getDouble() - Double.parseDouble(ref)) < 0.001;
        }
    }

    private void compatible_str(String tag, String ref, String jsonpathStr) {
        System.out.println("::::" + tag + " - " + jsonpathStr);

        ONode tmp = oNode.select(jsonpathStr);
        System.out.println(tmp.toJson());

        try {
            Object tmp2 = context.read(jsonpathStr);
            System.out.println(ONode.serialize(tmp2));

            if (tmp.toJson().equals(ONode.serialize(tmp2)) == false) {
                assert ref.length() == tmp.toJson().length();
            }
        } catch (Exception e) {
            System.out.println("jayway: err");
            assert ref.length() == tmp.toJson().length();
        }
    }
}