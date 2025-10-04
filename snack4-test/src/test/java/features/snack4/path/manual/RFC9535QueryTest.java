package features.path.manual;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 * @author noear 2025/5/5 created
 */
public class RFC9535QueryTest {
    static final String json1 = "{\n" +
            "  \"o\": {\"j j\": {\"k.k\": 3}},\n" +
            "  \"'\": {\"@\": 2}\n" +
            "}";

    @Test
    public void case1_1() {
        ONode rst = ONode.load(json1).select("$.o['j j']");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("{\"k.k\":3}");
    }

    @Test
    public void case1_2() {
        ONode rst = ONode.load(json1).select("$.o['j j']['k.k']");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("3");
    }

    static final String json2 = "{\n" +
            "  \"o\": {\"j\": 1, \"k\": 2},\n" +
            "  \"a\": [5, 3]\n" +
            "}";

    @Test
    public void case2_1() {
        ONode rst = ONode.load(json2).select("$[*]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("[{\"j\":1,\"k\":2},[5,3]]");
    }

    @Test
    public void case2_2() {
        ONode rst = ONode.load(json2).select("$.o[*]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("[1,2]");
    }

//    @Test
//    public void case2_3() {
//        ONode rst = ONode.load(json2).select("$.o[*,*]");
//        System.out.println(rst.toJson());
//        assert rst.toJson().equals("[1,2,1,2]");
//    }

    @Test
    public void case2_4() {
        ONode rst = ONode.load(json2).select("$.a[*]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("[5,3]");
    }

    static final String json3 = "[\"a\",\"b\"]";

    @Test
    public void case3_1() {
        ONode rst = ONode.load(json3).select("$[1]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("\"b\"");
    }

    @Test
    public void case3_2() {
        ONode rst = ONode.load(json3).select("$[-2]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("\"a\"");
    }


    static final String json4 = "[\"a\",\"b\",\"c\",\"d\",\"e\",\"f\",\"g\"]";

    @Test
    public void case4_1() {
        ONode rst = ONode.load(json4).select("$[1:3]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("[\"b\",\"c\"]");
    }

    @Test
    public void case4_2() {
        ONode rst = ONode.load(json4).select("$[5:]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("[\"f\",\"g\"]");
    }

    @Test
    public void case4_3() {
        ONode rst = ONode.load(json4).select("$[1:5:2]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("[\"b\",\"d\"]");
    }

    @Test
    public void case4_4() {
        ONode rst = ONode.load(json4).select("$[5:1:-2]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("[\"f\",\"d\"]");
    }

    @Test
    public void case4_5() {
        ONode rst = ONode.load(json4).select("$[::-1]");
        System.out.println(rst.toJson());
        assert rst.toJson().equals("[\"g\",\"f\",\"e\",\"d\",\"c\",\"b\",\"a\"]");
    }
}