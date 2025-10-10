package features.snack4.path.RFC9535;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 *
 * @author noear 2025/10/10 created
 *
 */
public class RFC9535_s2323 {

    static final String json2 = "{\n" +
            "  \"o\": {\"j\": 1, \"k\": 2},\n" +
            "  \"a\": [5, 3]\n" +
            "}";

    @Test
    public void case2_1() {
        ONode rst = ONode.ofJson(json2).select("$[*]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("[{\"j\":1,\"k\":2},[5,3]]");
    }

    @Test
    public void case2_2() {
        ONode rst = ONode.ofJson(json2).select("$.o[*]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("[1,2]");
    }


    @Test
    public void case2_3() {
        ONode rst = ONode.ofJson(json2).select("$.o[*,*]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("[1,2,1,2]");
    }

    @Test
    public void case2_4() {
        ONode rst = ONode.ofJson(json2).select("$.a[*]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("[5,3]");
    }
}
