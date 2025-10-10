package features.snack4.path.RFC9535;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 *
 * @author noear 2025/10/10 created
 *
 */
public class RFC9535_s2333 {

    static final String json3 = "[\"a\",\"b\"]";

    @Test
    public void case3_1() {
        ONode rst = ONode.ofJson(json3).select("$[1]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("\"b\"");
    }

    @Test
    public void case3_2() {
        ONode rst = ONode.ofJson(json3).select("$[-2]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("\"a\"");
    }
}
