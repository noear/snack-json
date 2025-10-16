package features.snack4.issue;

import com.jayway.jsonpath.JsonPath;
import features.snack4.jsonpath.manual.AbsQueryTest;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 * @author noear 2025/10/16 created
 */
public class Issue_IBL9SM extends AbsQueryTest {
    @Test
    public void case1() {
        System.out.println(ONode.ofJson(json).select("$..[?(@.price == '19.95')]"));
        System.out.println("------");
        System.out.println(ONode.ofJson(json).select("$..*[?(@.price == '19.95')]"));
    }

    @Test
    public void case2() {
        compatible_do("1", json, "$..[?(@.price == '19.95')]");
        compatible_do("2", json, "$..*[?(@.price == '19.95')]");
    }

    private void compatible_do(String hint, String json, String jsonpathStr) {
        System.out.println("::::" + hint);

        ONode tmp = ONode.ofJson(json).select(jsonpathStr);
        System.out.println(tmp.toJson());

        Object tmp2 = JsonPath.read(json, jsonpathStr);
        System.out.println(tmp2);

        assert tmp.toJson().equals(tmp2.toString());
    }

    static String json = "{\n" +
            "  \"store\": {\n" +
            "    \"book\": [\n" +
            "      {\n" +
            "        \"category\": \"reference\",\n" +
            "        \"author\": \"Nigel Rees\",\n" +
            "        \"title\": \"Sayings of the Century\",\n" +
            "        \"price\": 8.95\n" +
            "      },\n" +
            "      {\n" +
            "        \"category\": \"reference 19.95\",\n" +
            "        \"author\": \"Nigel Rees 19.95\",\n" +
            "        \"title\": \"Sayings of the Century 19.95\",\n" +
            "        \"price\": 19.95\n" +
            "      },\n" +
            "      {\n" +
            "        \"category\": \"fiction\",\n" +
            "        \"author\": \"Evelyn Waugh\",\n" +
            "        \"title\": \"Sword of Honour\",\n" +
            "        \"price\": 12.99\n" +
            "      },\n" +
            "      {\n" +
            "        \"category\": \"fiction\",\n" +
            "        \"author\": \"Herman Melville\",\n" +
            "        \"title\": \"Moby Dick\",\n" +
            "        \"isbn\": \"0-553-21311-3\",\n" +
            "        \"price\": 8.99\n" +
            "      },\n" +
            "      {\n" +
            "        \"category\": \"fiction\",\n" +
            "        \"author\": \"J. R. R. Tolkien\",\n" +
            "        \"title\": \"The Lord of the Rings\",\n" +
            "        \"isbn\": \"0-395-19395-8\",\n" +
            "        \"price\": 22.99\n" +
            "      }\n" +
            "    ],\n" +
            "    \"bicycle\": {\n" +
            "      \"color\": \"red\",\n" +
            "      \"price\": 19.95\n" +
            "    }\n" +
            "  },\n" +
            "  \"expensive\": 10\n" +
            "}";
}
