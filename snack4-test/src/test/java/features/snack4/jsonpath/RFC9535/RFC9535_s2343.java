package features.snack4.jsonpath.RFC9535;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 * @author noear 2025/5/5 created
 */
public class RFC9535_s2343 extends AbsRFC9535{
    // SQL/JSON Path (ISO/IEC 9075)
    // IETF JSONPath (RFC 9535) https://www.rfc-editor.org/rfc/rfc9535.html

    static final String json = "[\"a\",\"b\",\"c\",\"d\",\"e\",\"f\",\"g\"]";

    @Test
    public void case1() {
        ONode rst = ofJson(json).select("$[1:3]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("[\"b\",\"c\"]");
    }

    @Test
    public void case2() {
        ONode rst = ofJson(json).select("$[5:]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("[\"f\",\"g\"]");
    }

    @Test
    public void case3() {
        ONode rst = ofJson(json).select("$[1:5:2]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("[\"b\",\"d\"]");
    }

    @Test
    public void case4() {
        ONode rst = ofJson(json).select("$[5:1:-2]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("[\"f\",\"d\"]");
    }

    @Test
    public void case5() {
        ONode rst = ofJson(json).select("$[::-1]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("[\"g\",\"f\",\"e\",\"d\",\"c\",\"b\",\"a\"]");
    }
}