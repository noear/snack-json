package features.snack4.path.RFC9535;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.jsonpath.JsonPath;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author noear 2025/5/6 created
 */
public class RFC9535_s2353_Query {
    // SQL/JSON Path (ISO/IEC 9075)
    // IETF JSONPath (RFC 9535) https://www.rfc-editor.org/rfc/rfc9535.html

    @Test
    public void case1() {
        queryAssert("$.a[?@.b == 'kilo']", "[{\"b\": \"kilo\"}]");
        queryAssert("$.a[?(@.b == 'kilo')]", "[{\"b\": \"kilo\"}]");
        queryAssert("$.a[?@ > 3.5]", "[5,4,6]");
        queryAssert("$.a[?@.b]", "[{\"b\": \"j\"},{\"b\": \"k\"},{\"b\": {}},{\"b\": \"kilo\"}]");

        queryAssert("$.a[?@ < 2 || @.b == \"k\"]", "[1,{\"b\": \"k\"}]");

        queryAssert("$.o[?@ > 1 && @ < 4]", "[2,3]");
        queryAssert("$.o[?@.u || @.x]", "[{\"u\": 6}]");
        queryAssert("$.a[?@.b == $.x]", "[3,5,1,2,4,6]");
        queryAssert("$.a[?@ == @]", "[3,5,1,2,4,6,{\"b\": \"j\"},{\"b\": \"k\"},{\"b\": {}},{\"b\": \"kilo\"}]");


        queryAssert("$.*", "[[3,5,1,2,4,6,{\"b\":\"j\"},{\"b\":\"k\"},{\"b\":{}},{\"b\":\"kilo\"}],{\"p\":1,\"q\":2,\"r\":3,\"s\":5,\"t\":{\"u\":6}},\"f\"]");

        //Existence of non-singular queries
        queryAssert("$[?@.*]", "[[3,5,1,2,4,6,{\"b\":\"j\"},{\"b\":\"k\"},{\"b\":{}},{\"b\":\"kilo\"}],{\"p\":1,\"q\":2,\"r\":3,\"s\":5,\"t\":{\"u\":6}},\"f\"]");

        //Non-deterministic ordering
        queryAssert("$.o[?@ < 3, ?@ < 3]", "[1,2,1,2]"); //[1,2,2,1] 顺序不定

    }

    @Test
    public void case2() {
        //Nested filters
        queryAssert("$[?@[?@.b]]", "[[3, 5, 1, 2, 4, 6, {\"b\": \"j\"}, {\"b\": \"k\"}, {\"b\": {}}, {\"b\": \"kilo\"}]]");
    }

    @Test
    public void case3() {

        //Array value regular expression match
        queryAssert("$.a[?match(@.b, '[jk]')]", "[{\"b\": \"j\"},{\"b\": \"k\"}]");
        //Array value regular expression search
        queryAssert("$.a[?search(@.b, '[jk]')]", "[{\"b\": \"j\"},{\"b\": \"k\"},{\"b\": \"kilo\"}]");

    }

    private void queryAssert(String expr, String expected) {
        JsonPath jsonPath = JsonPath.compile(expr);

        String actual = jsonPath.select(ONode.ofJson(json, Options.of().RFC9535(true))).toJson();
        String expected2 = ONode.ofJson(expected).toJson(); //重新格式化
        System.out.println("::" + expr);
        assertEquals(expected2, actual);
    }

    static final String json = "{\n" +
            "  \"a\": [3, 5, 1, 2, 4, 6,\n" +
            "        {\"b\": \"j\"},\n" +
            "        {\"b\": \"k\"},\n" +
            "        {\"b\": {}},\n" +
            "        {\"b\": \"kilo\"}\n" +
            "       ],\n" +
            "  \"o\": {\"p\": 1, \"q\": 2, \"r\": 3, \"s\": 5, \"t\": {\"u\": 6}},\n" +
            "  \"e\": \"f\"\n" +
            "}";
}
