package features.snack4.path.RFC9535;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONAware;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 *
 * @author noear 2025/10/11 created
 *
 */
public class RFC9535_s1500 extends AbsRFC9535{
    // SQL/JSON Path (ISO/IEC 9075)
    // IETF JSONPath (RFC 9535) https://www.rfc-editor.org/rfc/rfc9535.html


    @Test
    public void case1() {
        queryCompatibleDo("$.store.book[*].author"); //书店里所有书籍的作者
        queryCompatibleDo("$..author"); //所有作者
        queryCompatibleDo("$.store.*"); //商店里的所有东西，包括一些书和一辆红色自行车
        queryCompatibleDo("$.store..price"); //商店里所有商品的价格
        queryCompatibleDo("$..book[2]"); //商店里所有商品的价格
        queryCompatibleDo("$..book[2].author"); //第三本书
        queryCompatibleDo("$..book[2].publisher"); //第三本书的作者
        queryCompatibleDo("$..book[-1]"); //空结果：第三本书没有“出版商”成员
        queryCompatibleDo("$..book[0,1]"); //按顺序排列的最后一本书
        queryCompatibleDo("$..book[:2]"); //前两本书
        queryCompatibleDo("$..book[?(@.isbn)]"); //所有具有 ISBN 编号的书籍
        queryCompatibleDo("$..book[?(@.price < 10)]"); //所有低于 10 的书籍

        queryNoCompatibleDo("$..book[?@.isbn]"); //所有具有 ISBN 编号的书籍
        queryNoCompatibleDo("$..book[?@.price < 10]"); //所有低于 10 的书籍
    }

    @Test
    public void case2() {
        queryCompatibleDo2("$..*"); //输入值中包含的所有成员值和数组元素
    }

    private ONode queryCompatibleDo(String expr) {
        ONode oNode = ofJson(json).select(expr);
        String rst1 = oNode.toJson();
        System.out.println("::" + expr);
        System.out.println(rst1);

        JSONAware jsonAware = JsonPath.read(json, expr);
        String rst2 = jsonAware.toJSONString();
        System.out.println(rst2);

        assert rst2.equals(rst1);

        return oNode;
    }

    private ONode queryCompatibleDo2(String expr) {
        ONode oNode = ofJson(json).select(expr);
        String rst1 = oNode.toJson();
        System.out.println("::" + expr);
        System.out.println(rst1);

        JSONAware jsonAware = JsonPath.read(json, expr);
        String rst2 = jsonAware.toJSONString();
        System.out.println(rst2);

        assert rst2.equals(rst1) || rst1.length() == rst2.length();

        return oNode;
    }

    private ONode queryNoCompatibleDo(String expr) {
        ONode oNode = ofJson(json).select(expr);
        System.out.println("::" + expr);
        System.out.println(oNode.toJson());

        return oNode;
    }


    static final String json = "{ \"store\": {\n" +
            "    \"book\": [\n" +
            "      { \"category\": \"reference\",\n" +
            "        \"author\": \"Nigel Rees\",\n" +
            "        \"title\": \"Sayings of the Century\",\n" +
            "        \"price\": 8.95\n" +
            "      },\n" +
            "      { \"category\": \"fiction\",\n" +
            "        \"author\": \"Evelyn Waugh\",\n" +
            "        \"title\": \"Sword of Honour\",\n" +
            "        \"price\": 12.99\n" +
            "      },\n" +
            "      { \"category\": \"fiction\",\n" +
            "        \"author\": \"Herman Melville\",\n" +
            "        \"title\": \"Moby Dick\",\n" +
            "        \"isbn\": \"0-553-21311-3\",\n" +
            "        \"price\": 8.99\n" +
            "      },\n" +
            "      { \"category\": \"fiction\",\n" +
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
}