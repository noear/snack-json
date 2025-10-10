package features.snack4.path.RFC9535;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 *
 * @author noear 2025/10/10 created
 *
 */
public class RFC9535_s2313 {
    // https://www.rfc-editor.org/rfc/rfc9535.html

    static final String json1 = "{\n" +
            "  \"o\": {\"j j\": {\"k.k\": 3}},\n" +
            "  \"a\": {\"@\": 2}\n" +
            "}";

    @Test
    public void case1_1() {
        ONode rst = ONode.ofJson(json1).select("$.o['j j']");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("{\"k.k\":3}");
    }

    @Test
    public void case1_2() {
        ONode rst = ONode.ofJson(json1).select("$.o['j j']['k.k']");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("3");
    }

    @Test
    public void case1_3() {
        ONode rst = ONode.ofJson(json1).select("$['a']['@']"); // $['\'']['@']
        System.out.println(rst.toJson());
        assert rst.toJson().equals("2");
    }
}
