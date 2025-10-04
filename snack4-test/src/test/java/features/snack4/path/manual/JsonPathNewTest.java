package features.path.manual;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 * @author noear 2023/3/4 created
 */
public class JsonPathNewTest {
    @Test
    public void test1() {
        ONode oNode = new ONode();
        oNode.create("$.orders[0].price").setValue(500);
        System.out.println(oNode.toJson());
        //{"orders":[{"price":500}]}

        assert "{\"orders\":[{\"price\":500}]}".equals(oNode.toJson());


        oNode.create("$.orders[10].price").setValue(600);
        System.out.println(oNode.toJson());
        //{"orders":[{"price":500},null,null,null,null,null,null,null,null,null,{"price":600}]}

        oNode.select("$.orders").getArray().stream().filter(n -> n.isObject());
        System.out.println(oNode.toJson());
        //{"orders":[{"price":500},{},{},{},{},{},{},{},{},{},{"price":600}]}
    }

    public void test2(){

    }
}
