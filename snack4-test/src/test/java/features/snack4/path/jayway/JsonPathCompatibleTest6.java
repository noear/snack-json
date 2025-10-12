package features.snack4.path.jayway;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.Standard;

/**
 * @author noear 2023/5/13 created
 */
public class JsonPathCompatibleTest6 {
    @Test
    public void test1() {
        String test = "{\"1\":{\"a1\":[{\"id\":\"a1\"},{\"id\":\"a2\"}],\"b1\":[{\"id\":\"b1\"},{\"id\":\"b2\"}]},\"2\":{\"a2\":[{\"id\":\"a1\",\"id1\":\"a11\",\"userId\":\"a12\"},{\"id\":\"a2\"}],\"b2\":[{\"id\":\"b1\"},{\"id\":\"b2\"}]}}";
        String jsonPath = "$..*[?(@.id)]";
        String json1 = ONode.ofJson(test, Options.of().addStandard(Standard.JSONPath_Jayway)).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.serialize(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test1_2() {
        String test = "{\"1\":{\"a1\":[{\"id\":\"a1\"},{\"id\":\"a2\"}],\"b1\":[{\"id\":\"b1\"},{\"id\":\"b2\"}]},\"2\":{\"a2\":[{\"id\":\"a1\",\"id1\":\"a11\",\"userId\":\"a12\"},{\"id\":\"a2\"}],\"b2\":[{\"id\":\"b1\"},{\"id\":\"b2\"}]}}";
        String jsonPath = "$..[?(@.id)]";
        String json1 = ONode.ofJson(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.serialize(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test2() {
        String test = "{\"1\":{\"a1\":[{\"id\":\"a1\"},{\"id\":\"a2\"}],\"b1\":[{\"id\":\"b1\"},{\"id\":\"b2\"}]},\"2\":{\"a2\":[{\"id\":\"a1\",\"id1\":\"a11\",\"userId\":\"a12\"},{\"id\":\"a2\"}],\"b2\":[{\"id\":\"b1\"},{\"id\":\"b2\"}]}}";
        String jsonPath = "$.*.*";
        String json1 = ONode.ofJson(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.serialize(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test2_2() {
        String test = "{\"1\":{\"a1\":[{\"id\":\"a1\"},{\"id\":\"a2\"}],\"b1\":[{\"id\":\"b1\"},{\"id\":\"b2\"}]},\"2\":{\"a2\":[{\"id\":\"a1\",\"id1\":\"a11\",\"userId\":\"a12\"},{\"id\":\"a2\"}],\"b2\":[{\"id\":\"b1\"},{\"id\":\"b2\"}]}}";
        String jsonPath = "$.*.*.*";
        String json1 = ONode.ofJson(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.serialize(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test2_2_2() {
        String test = "{\"1\":{\"a1\":[{\"id\":\"a1\"},{\"id\":\"a2\"}],\"b1\":[{\"id\":\"b1\"},{\"id\":\"b2\"}]},\"2\":{\"a2\":[{\"id\":\"a1\",\"id1\":\"a11\",\"userId\":\"a12\"},{\"id\":\"a2\"}],\"b2\":[{\"id\":\"b1\"},{\"id\":\"b2\"}]}}";
        String jsonPath = "$.[*].[*].[*]";
        String json1 = ONode.ofJson(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.serialize(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test3() {
        String test = "[1,2,3]";
        String jsonPath = "$.*";
        String json1 = ONode.ofJson(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.serialize(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test3_2() {
        String test = "[1,2,3]";
        String jsonPath = "$.[*]";
        String json1 = ONode.ofJson(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.serialize(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test4() {
        String test = "[{a:1},{a:2},{a:3}]";
        String jsonPath = "$.*";
        String json1 = ONode.ofJson(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.serialize(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test4_2() {
        String test = "[{a:1},{a:2},{a:3}]";
        String jsonPath = "$.[*]";
        String json1 = ONode.ofJson(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.serialize(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test5() {
        String test = "[{a:1},{a:2},{a:3}]";
        String jsonPath = "$.*.*";
        String json1 = ONode.ofJson(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.serialize(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test5_2() {
        String test = "[{a:1},{a:2},{a:3}]";
        String jsonPath = "$.[*].[*]";
        String json1 = ONode.ofJson(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.serialize(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test6() {
        String test = "[{a:1},{a:2},{a:3}]";
        String jsonPath = "$.*.[*]";
        String json1 = ONode.ofJson(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.serialize(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test7() {
        String test = "[{\"field\":\"l1-field-1\",\"children\":[{\"field\":\"l2-field-1\",\"fields\":[{\"field\":\"l3-field-1\"},{\"field\":\"l3-field-2\"}]},{\"field\":\"l2-field-2\",\"fields\":[{\"field\":\"l3-field-1\"},{\"field\":\"l3-field-2\"}]}]},{\"field\":\"l1-field-2\",\"children\":[{\"field\":\"l2-field-1\",\"fields\":[{\"field\":\"l3-field-1\"},{\"field\":\"l3-field-2\"}]},{\"field\":\"l2-field-2\",\"fields\":[{\"field\":\"l3-field-1\"},{\"field\":\"l3-field-2\"}]}]}]";

        String jsonPath = "$.[?(@.field == 'l1-field-1')].children[?(@.field == 'l2-field-1')]";
        String json1 = ONode.ofJson(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.serialize(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();

    }

    @Test
    public void test8() {
        String test = "[{\"field\":\"l1-field-1\",\"children\":[{\"field\":\"l2-field-1\",\"fields\":[{\"field\":\"l3-field-1\"},{\"field\":\"l3-field-2\"}]},{\"field\":\"l2-field-2\",\"fields\":[{\"field\":\"l3-field-1\"},{\"field\":\"l3-field-2\"}]}]},{\"field\":\"l1-field-2\",\"children\":[{\"field\":\"l2-field-1\",\"fields\":[{\"field\":\"l3-field-1\"},{\"field\":\"l3-field-2\"}]},{\"field\":\"l2-field-2\",\"fields\":[{\"field\":\"l3-field-1\"},{\"field\":\"l3-field-2\"}]}]}]";

        String jsonPath = "$[?(@.field == 'l1-field-1')].children[?(@.field == 'l2-field-1')].fields[*]";
        String json1 = ONode.ofJson(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.serialize(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();

    }

    @Test
    public void test9() {
        String json = "{\"request1\":{\"result\":[{\"relTickers\":[{\"tickerId\":1},{\"tickerId\":1.1}],\"accountId\":400006},{\"relTickers\":[{\"tickerId\":2},{\"tickerId\":2.2}]},{\"relTickers\":[{\"tickerId\":3}]},{\"relTickers\":[{\"tickerId\":4}]},{\"relTickers\":[{\"tickerId\":5}]},{\"relTickers\":[{\"tickerId\":6}]}]}}\n";

        assert_do("1", json, "$.request1..tickerId");
        assert_do("2", json, "$.request1..tickerId.first()");
        assert_do("3", json, "$.request1..tickerId.last()");

        assert_do("4", json, "$.request1.result[*].relTickers[*].tickerId.first()");
        assert_do("5", json, "$.request1.result[*].relTickers[*].tickerId.last()");

        System.out.println(ONode.ofJson(json).select("$.request1.result[*].relTickers.first()"));

        //todo: jayway 可能不支持函数之后，再查询
        //assert_do("6", json, "$.request1.result[*].relTickers.first()[0].tickerId");
        //assert_do("7", json, "$.request1.result[*].relTickers.last()[0].tickerId");
    }

    private void assert_do(String hint, String json, String jsonpathStr) {
        System.out.println("::::" + hint);
        ONode tmp = null;
        Object tmp2 = null;
        Throwable err1 = null;
        Throwable err2 = null;

        try {
            tmp = ONode.ofJson(json, Options.of().addStandard(Standard.JSONPath_Jayway)).select(jsonpathStr);
            System.out.println(tmp.toJson());
        } catch (Throwable ex) {
            err1 = ex;
            System.err.println(ex.getMessage());
        }

        try {
            tmp2 = JsonPath.read(json, jsonpathStr);
            System.out.println(tmp2);
        } catch (Throwable ex) {
            err2 = ex;
            System.err.println(ex.getMessage());
        }

        if (err1 != null && err2 != null) {
            return;
        }

        assert tmp.toJson().equals(tmp2.toString());
    }

    @Test
    public void test10_1() {
        String json = "{\"result\":[]}";

        ONode oNode = ONode.ofJson(json).select("$.result[*].amount.sum()");
        System.out.println(oNode.toString());

        assert oNode.getLong() == 0L;
    }

    @Test
    public void test10_2() {
        String json = "{\"result\":[]}";

        ONode oNode = ONode.ofJson(json).select("$.result[*].amount.min()");
        System.out.println(oNode.toString());

        assert oNode.getLong() == 0L;
    }

    @Test
    public void test10_3() {
        String json = "{\"result\":[]}";

        ONode oNode = ONode.ofJson(json).select("$.result[*].amount.max()");
        System.out.println(oNode.toString());

        assert oNode.getLong() == 0L;


        System.out.println(ONode.ofJson(json).select("$.result[*].amount.max()").pathList());
        assert ONode.ofJson(json).select("$.result[*].amount.max()").pathList().size() == 0;
    }

    @Test
    public void test11() {
        //示例代码：
        ONode oNode = ONode.ofJson("{\n" +
                "  \"projectCode\" : \"IS0101\",\n" +
                "  \"columns\" : [ {\n" +
                "    \"columnCode\" : \"#YEAR(-1)#YTD#Period#Actual#ACCOUNT#[ICP None]#[None]#[None]#REPORT#PRCTotal\",\n" +
                "    \"value\" : \"1\"\n" +
                "  } ]\n" +
                "}");

        Object rst1 = oNode.select("$.columns[?(@.columnCode == '#YEAR(-1)#YTD#Period#Actual#ACCOUNT#[ICP None]#[None]#[None]#REPORT#PRCTotal')].first()");

        System.out.println(rst1);

        //使用JsonPath正常
        Object rst2 = JsonPath.parse("   {\n" +
                "  \"projectCode\" : \"IS0101\",\n" +
                "  \"columns\" : [ {\n" +
                "    \"columnCode\" : \"#YEAR(-1)#YTD#Period#Actual#ACCOUNT#[ICP None]#[None]#[None]#REPORT#PRCTotal\",\n" +
                "    \"value\" : \"1\"\n" +
                "  } ]\n" +
                "}").read("$.columns[?(@.columnCode == '#YEAR(-1)#YTD#Period#Actual#ACCOUNT#[ICP None]#[None]#[None]#REPORT#PRCTotal')]");
        System.out.println(rst2);
    }
}
