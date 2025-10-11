package features.snack4.path.RFC9535;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 *
 * @author noear 2025/10/10 created
 *
 */
public class RFC9535_s2313 extends AbsRFC9535{
    // SQL/JSON Path (ISO/IEC 9075)
    // IETF JSONPath (RFC 9535) https://www.rfc-editor.org/rfc/rfc9535.html

    static final String json = "{\n" +
            "  \"o\": {\"j j\": {\"k.k\": 3}},\n" +
            "  \"a\": {\"@\": 2}\n" +
            "}";

    @Test
    public void case1() {
        ONode rst = ofJson(json).select("$.o['j j']");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("{\"k.k\":3}");
    }

    @Test
    public void case2() {
        ONode rst = ofJson(json).select("$.o['j j']['k.k']");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("3");
    }

    @Test
    public void case3() {
        ONode rst = ofJson(json).select("$['a']['@']"); // $['\'']['@']
        System.out.println(rst.toJson());
        assert rst.toJson().equals("2");
    }
}
