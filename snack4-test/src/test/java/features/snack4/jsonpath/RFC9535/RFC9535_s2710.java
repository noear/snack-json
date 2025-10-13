package features.snack4.jsonpath.RFC9535;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 *
 * @author noear 2025/10/11 created
 *
 */
public class RFC9535_s2710 extends AbsRFC9535 {
    @Test
    public void case1() {
        queryAssert("{'a':1}", "$.a", "$['a']");
        queryAssert("[1,2,3]", "$[1]", "$[1]");
        queryAssert("[1,2,3,4,5]", "$[-3]", "$[2]");
    }

    @Test
    public void case2() {
        queryOf("{'a':{b:[1,2,3,4,5]}}", "$.a.b[1:2]").equals(new ONode().add(2));
        queryOf("{'a':{b:[1,2,3,4,5]}}", "$['a']['b'][1]").equals(2);
    }

    @Test
    public void case3() {
        queryAssert("{'a':1}", "$['\\u000B']", "$['\\u000b']");
        queryAssert("{'a':1}", "$['\\u0061']", "$['a']");
    }

    private void queryAssert(String json, String expr1, String expr2) {
        ONode oNode = ofJson(json);
        String rst1 = oNode.select(expr1).toJson();
        String rst2 = oNode.select(expr2).toJson();

        System.out.println("::" + expr1 + " - " + expr2);
        System.out.println(rst1);
        System.out.println(rst2);

        Assertions.assertEquals(rst1, rst2);
    }
}
