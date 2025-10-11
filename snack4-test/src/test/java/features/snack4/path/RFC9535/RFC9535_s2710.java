package features.snack4.path.RFC9535;

/**
 *
 * @author noear 2025/10/11 created
 *
 */
public class RFC9535_s2710 {
    public void case1() {
        queryAssert("$.a", "$['a']");
        queryAssert("$[1]", "$[1]");
        queryAssert("$[-3]", "$[2]");
        queryAssert("$.a.b[1:2]", "$['a']['b'][1]");
        queryAssert("$['\\u000B']", "$['\\u000b']");
        queryAssert("$['\\u0061']", "$['a']");
    }

    private void queryAssert(String expr, String expected) {

    }
}
