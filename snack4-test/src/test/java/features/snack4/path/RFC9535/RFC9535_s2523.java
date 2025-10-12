package features.snack4.path.RFC9535;

import org.junit.jupiter.api.Test;
import org.noear.snack4.jsonpath.JsonPath;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author noear 2025/10/11 created
 *
 */
public class RFC9535_s2523 extends AbsRFC9535{
    @Test
    public void case1() {
        queryAssert("$..j", "[1,4]");
        queryAssert("$..[0]", "[5,{\"j\":4}]");

        queryAssert2("$..[*]", "[{\"j\":1,\"k\":2},[5,3,[{\"j\":4},{\"k\":6}]],1,2,5,3,[{\"j\":4},{\"k\":6}],{\"j\":4},{\"k\":6},4,6]");
        queryAssert2("$..*", "[{\"j\":1,\"k\":2},[5,3,[{\"j\":4},{\"k\":6}]],1,2,5,3,[{\"j\":4},{\"k\":6}],{\"j\":4},{\"k\":6},4,6]");

        queryAssert("$..o", "[{\"j\":1,\"k\":2}]");

        queryAssert("$.o..[*, *]", "[1,2,1,2]"); //[1,2,2,1] 顺序不一定
    }

    @Test
    public void case2() {
        queryAssert("$.a..[0, 1]", "[5,{\"j\":4},3,{\"k\":6}]"); //[5,3,{"j":4},{"k":6}] 顺序不一定
    }

    private void queryAssert(String expr, String expected) {
        JsonPath jsonPath = JsonPath.parse(expr);

        String actual = jsonPath.select(ofJson(json)).toJson();
        String expected2 = ofJson(expected).toJson(); //重新格式化
        System.out.println("::" + expr);
        assertEquals(expected2, actual);
    }

    private void queryAssert2(String expr, String expected) {
        JsonPath jsonPath = JsonPath.parse(expr);

        String actual = jsonPath.select(ofJson(json)).toJson();
        String expected2 = ofJson(expected).toJson(); //重新格式化
        System.out.println("::" + expr);

        assert expected2.equals(actual) || expected2.length() == actual.length();
    }

    static final String json = "{\n" +
            "  \"o\": {\"j\": 1, \"k\": 2},\n" +
            "  \"a\": [5, 3, [{\"j\": 4}, {\"k\": 6}]]\n" +
            "}";
}
