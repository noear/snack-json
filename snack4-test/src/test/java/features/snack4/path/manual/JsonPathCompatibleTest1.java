package features.path.manual;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import features.path.generated.JsonPathSelectComplexTest;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author noear 2023/11/3 created
 */
public class JsonPathCompatibleTest1 {
    @Test
    public void test1() {
        String json = "[{\"id\":0,\"treePath\":\"1\",\"a\":[{\"id\":1,\"treePath\":\"123\",\"subItem\":[{\"id\":3,\"treePath\":\"123\"}]}],\"b\":\"a\"},{\"id\":2}]";

        ReadContext context = JsonPath.parse(json);

        JSONArray tmp = context.read("$..*");
        System.out.println(tmp.toJSONString());
        assert tmp.size() == 14;

        ONode tmp2 = ONode.load(json).select("$..*");
        System.out.println(tmp2);
        assert tmp2.isArray();
        assertEquals(14, tmp2.size());
    }

    @Test
    public void test2() {
        String json = "[{\"id\":0,\"treePath\":\"1\",\"a\":[{\"id\":1,\"treePath\":\"123\",\"subItem\":[{\"id\":3,\"treePath\":\"123\"}]}],\"b\":\"a\"},{\"id\":2}]";

        ReadContext context = JsonPath.parse(json);

        JSONArray tmp = context.read("$..*[?(@.treePath)]");
        System.out.println(tmp);
        assert tmp.size() == 5;

        ONode tmp2 = ONode.load(json).select("$..*[?(@.treePath)]");
        System.out.println(tmp2);
        assert tmp2.isArray();
        assert tmp2.size() == 5;
    }

    @Test
    public void test3() {
        String json = "[{\"id\":0,\"treePath\":\"1\",\"a\":[{\"id\":1,\"treePath\":\"123\",\"subItem\":[{\"id\":3,\"treePath\":\"123\"}]}],\"b\":\"a\"},{\"id\":2}]";

        ReadContext context = JsonPath.parse(json);

        JSONArray tmp = context.read("$..[?(@.treePath)]");
        System.out.println(tmp);
        assert tmp.size() == 3;

        ONode tmp2 = ONode.load(json).select("$..[?(@.treePath)]");
        System.out.println(tmp2);
        assertEquals(3,  tmp2.size());
    }

    @Test
    public void test4() {
        String json = "{\"request1\":{\"result\":[{\"relTickers\":[{\"tickerId\":1},{\"tickerId\":1.1}],\"accountId\":400006},{\"relTickers\":[{\"tickerId\":2},{\"tickerId\":2.2}]},{\"relTickers\":[{\"tickerId\":3}]},{\"relTickers\":[{\"tickerId\":4}]},{\"relTickers\":[{\"tickerId\":5}]},{\"relTickers\":[{\"tickerId\":6}]}]}}\n";

        String jsonpathStr1 = "$.request1.result[*]";
        String jsonpathStr2 = "$.request1.result[*].relTickers";
        String jsonpathStr3 = "$.request1.result[*].relTickers[0]";
        String jsonpathStr4 = "$.request1.result[*].relTickers[0].tickerId";

        compatible_do("1", json, jsonpathStr1);
        compatible_do("2", json, jsonpathStr2);
        compatible_do("3", json, jsonpathStr3);
        compatible_do("4", json, jsonpathStr4);
    }

    @Test
    public void test5() {
        String json = "{\"questionAnswerListMap\":{\"Q1\":[{\"qCode\":\"Q1\",\"qaIndex\":1,\"answerItem\":{\"qIndex\":1,\"qRow\":0,\"qColumn\":1,\"title\":\"1) Q1 . 姓名___\",\"itemValue\":0.0,\"answerText\":\"测试\"}},{\"qCode\":\"Q1\",\"qaIndex\":2,\"answerItem\":{\"qIndex\":1,\"qRow\":0,\"qColumn\":2,\"title\":\"2) 手机号___\",\"itemValue\":0.0,\"answerText\":\"15812341234\"}}],\"A10103A\":[{\"qCode\":\"A10103A\",\"qaIndex\":1,\"answerItem\":{\"qIndex\":4,\"qRow\":0,\"qColumn\":1,\"title\":\"1) A10103A.   1.身体形态，体质指数（BMI）（kg/m2）  当前体重___\",\"itemValue\":0.0,\"answerText\":\"70\"}}],\"A10104\":[{\"qCode\":\"A10104\",\"qaIndex\":0,\"answerItem\":{\"qIndex\":5,\"qRow\":0,\"qColumn\":0,\"title\":\"A10104.最近一个月体重波动？\",\"itemIndex\":[1],\"itemValue\":1.0,\"answerText\":\"A. 升高\"}},{\"qCode\":\"A10104\",\"qaIndex\":1,\"answerItem\":{\"qIndex\":5,\"qRow\":0,\"qColumn\":1,\"title\":\"A10104.最近一个月体重波动？\",\"itemValue\":1.0,\"answerText\":\"A. 升高〖1〗\"}}]}}";

        String jsonpathStr1 = "$..Q1[?(@.qaIndex == 1)].answerItem.answerText";
        String jsonpathStr2 = "$..Q1[?(@.qaIndex == 1)][0].answerItem.answerText";
        String jsonpathStr3 = "$..Q1[?(@.qaIndex == 1)].answerItem.answerText[0]";

        compatible_do("1", json, jsonpathStr1);
        compatible_do("2", json, jsonpathStr2);
        compatible_do("3", json, jsonpathStr3);
    }

    @Test
    public void test6() {
        String json = "{\"numbers\":[1,3,4,7,-1]}";

        String jsonpathStr1 = "$.numbers.sum()";
        compatible_do("1", json, jsonpathStr1);

        String jsonpathStr2 = "$.numbers.avg()";
        compatible_do("2", json, jsonpathStr2);
    }


//    @Test
//    public void test6_1() {
//        String json = "{\"result\":[]}";
//
//        String jsonpathStr1 = "$.result[*].amount.sum()";
//
//        compatible_do("1", json, jsonpathStr1);
//    }
//
//    @Test
//    public void test6_2() {
//        String json = "{\"result\":[]}";
//        String jsonpathStr1 = "$.result[*].amount.min()";
//
//        compatible_do("1", json, jsonpathStr1);
//    }
//
//    @Test
//    public void test6_3() {
//        String json = "{\"result\":[]}";
//
//        String jsonpathStr1 = "$.result[*].amount.max()";
//
//        compatible_do("1", json, jsonpathStr1);
//    }

    @Test
    public void test7() {
        //1.加载json
        String json = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");

        String jsonpathStr1 = "$.*.list[0][0]";
        String jsonpathStr2 = "$..list[0][0]";

        compatible_do("1", json, jsonpathStr1);
        compatible_do("2", json, jsonpathStr2);
    }

    @Test
    public void test8() {
        final String json = "{\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95},{\"category\":\"fiction\",\"author\":\"Evelyn Waugh\",\"title\":\"Sword of Honour\",\"price\":12.99},{\"category\":\"fiction\",\"author\":\"Herman Melville\",\"title\":\"Moby Dick\",\"isbn\":\"0-553-21311-3\",\"price\":8.99},{\"category\":\"fiction\",\"author\":\"J. R. R. Tolkien\",\"title\":\"The Lord of the Rings\",\"isbn\":\"0-395-19395-8\",\"price\":22.99}],\"bicycle\":{\"color\":\"red\",\"price\":19.95}},\"expensive\":10}";

        String jsonpathStr1 = "$..book[2]";

        compatible_do("1", json, jsonpathStr1);
    }

    @Test
    public void test9() {
        final String json = "{\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95},{\"category\":\"fiction\",\"author\":\"Evelyn Waugh\",\"title\":\"Sword of Honour\",\"price\":12.99},{\"category\":\"fiction\",\"author\":\"Herman Melville\",\"title\":\"Moby Dick\",\"isbn\":\"0-553-21311-3\",\"price\":8.99},{\"category\":\"fiction\",\"author\":\"J. R. R. Tolkien\",\"title\":\"The Lord of the Rings\",\"isbn\":\"0-395-19395-8\",\"price\":22.99}],\"bicycle\":{\"color\":\"red\",\"price\":19.95}},\"expensive\":10}";

        String jsonpathStr1 = "$..book[?(@.author =~ /.*REES/i)]";

        compatible_do("1", json, jsonpathStr1);
    }

    @Test
    public void test10(){
        String json = "{\"request1\":{\"result\":[{\"relTickers\":[{\"tickerId\":1},{\"tickerId\":1.1}],\"accountId\":400006},{\"relTickers\":[{\"tickerId\":2},{\"tickerId\":2.2}]},{\"relTickers\":[{\"tickerId\":3}]},{\"relTickers\":[{\"tickerId\":4}]},{\"relTickers\":[{\"tickerId\":5}]},{\"relTickers\":[{\"tickerId\":6}]}]}}\n";


        String jsonpathStr3   = "$.request1.result[*].relTickers.first().tickerId";
        String jsonpathStr3_b = "$.request1.result[*].relTickers.last().tickerId";

        compatible_do("1", json, jsonpathStr3);
        compatible_do("2", json, jsonpathStr3_b);
    }

    @Test
    public void test11(){
        String jsonpathStr1 = "$.store.book.size()";
        compatible_do("1", JsonPathSelectComplexTest.JSON, jsonpathStr1);
    }


    private void compatible_do(String hint, String json, String jsonpathStr) {
        System.out.println("::::" + hint);

        ONode tmp = ONode.load(json).select(jsonpathStr);
        System.out.println(tmp.toJson());

        Object tmp2 = JsonPath.read(json, jsonpathStr);
        System.out.println(tmp2);

        assert tmp.toJson().equals(tmp2.toString());
    }
}
