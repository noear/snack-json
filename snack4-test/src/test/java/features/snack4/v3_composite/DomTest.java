package features.snack4.v3_composite;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 * @author noear 2023/1/19 created
 */
public class DomTest {
    @Test
    public void ary() {
        ONode oNode = new ONode();
        oNode.getOrNew(2, n -> n.asObject());

        String json = oNode.toJson();

        System.out.println(json);

        assert ONode.ofJson(json).get(1).isObject();
    }

    @Test
    public void obj(){
        ONode oNode = new ONode();
        oNode.getOrNew("n1").asObject();

        String json = oNode.toJson();

        System.out.println(json);

        assert ONode.ofJson(json).get("n1").isObject();
    }
}
