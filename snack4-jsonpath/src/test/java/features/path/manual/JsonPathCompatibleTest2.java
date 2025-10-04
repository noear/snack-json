package features.path.manual;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 * @author noear 2023/11/3 created
 */
public class JsonPathCompatibleTest2 {
    static final String json = "{\n" +
            "  \"store\": {\n" +
            "    \"book\": [\n" +
            "      {\n" +
            "        \"category\": \"reference\",\n" +
            "        \"author\": \"Nigel Rees\",\n" +
            "        \"title\": \"Sayings of the Century\",\n" +
            "        \"price\": 8.95\n" +
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
            "      \"price\": 399\n" +
            "    }\n" +
            "  }\n" +
            "}";

    @Test
    public void test1() {
        compatible_do("1", json, "$.store.book[*].author");
    }

    @Test
    public void test2() {
        compatible_do("1", json, "$..author");
    }


    @Test
    public void test3() {
        compatible_do("1", json, "$.store.*");
    }


    @Test
    public void test4() {
        compatible_do("1", json, "$.store..price");
    }

    @Test
    public void test5() {
        compatible_do("1", json, "$..book[2]");
    }

    @Test
    public void test6() {
        compatible_do("1", json, "$..book[2].author");
    }

    @Test
    public void test7() {
        compatible_do("1", json, "$..book[2].publisher");
    }

    @Test
    public void test8() {
        compatible_do("1", json, "$..book[-1]");
    }

    @Test
    public void test9() {
        compatible_do("1", json, "$..book[0,1]");
    }

    @Test
    public void test10() {
        compatible_do("1", json, "$..book[:2]");
    }

//    @Test
//    public void test11() {
//        compatible_do("1", json, "$..book[?@.isbn]"); //jayway 不支持没有括号的
//    }

    @Test
    public void test11_2() {
        compatible_do("1", json, "$..book[?(@.isbn)]");
    }

//    @Test
//    public void test12() {
//        compatible_do("1", json, "$..book[?@.price < 10]"); //jayway 不支持没有括号的
//    }


    @Test
    public void test12_2() {
        compatible_do("1", json, "$..book[?(@.price < 10)]");
    }

//    @Test
//    public void test13() {
//        compatible_do("1", json, "$..*"); //数据相同，但顺序不同
//    }


    private void compatible_do(String hint, String json, String jsonpathStr) {
        System.out.println("::::" + hint);

        ONode tmp = ONode.load(json).select(jsonpathStr);
        System.out.println(tmp.toJson());

        Object tmp2 = JsonPath.read(json, jsonpathStr);
        System.out.println(tmp2);

        assert tmp.toJson().equals(tmp2.toString());
    }
}