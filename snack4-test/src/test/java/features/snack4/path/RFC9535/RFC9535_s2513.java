package features.snack4.path.RFC9535;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.jsonpath.JsonPath;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author noear 2025/10/11 created
 *
 */
public class RFC9535_s2513 {
    @Test
    public void case1() {
        queryAssert("$[0, 3]", "['a','d']");
        queryAssert("$[0:2, 5]", "['a','b','f']");
        queryAssert("$[0, 0]", "['a','a']");
    }

    private void queryAssert(String expr, String expected) {
        JsonPath jsonPath = JsonPath.compile(expr);

        String actual = jsonPath.select(ONode.ofJson(json, Options.of().RFC9535(true))).toJson();
        String expected2 = ONode.ofJson(expected).toJson(); //重新格式化
        System.out.println("::" + expr);
        assertEquals(expected2, actual);
    }

    static final String json = "[\"a\", \"b\", \"c\", \"d\", \"e\", \"f\", \"g\"]";
}
