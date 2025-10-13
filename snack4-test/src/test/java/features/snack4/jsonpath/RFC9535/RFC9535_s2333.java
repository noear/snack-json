package features.snack4.jsonpath.RFC9535;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 *
 * @author noear 2025/10/10 created
 *
 */
public class RFC9535_s2333 extends AbsRFC9535{
    // SQL/JSON Path (ISO/IEC 9075)
    // IETF JSONPath (RFC 9535) https://www.rfc-editor.org/rfc/rfc9535.html

    static final String json = "[\"a\",\"b\"]";

    @Test
    public void case1() {
        ONode rst = ofJson(json).select("$[1]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("\"b\"");
    }

    @Test
    public void case2() {
        ONode rst = ofJson(json).select("$[-2]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("\"a\"");
    }
}
