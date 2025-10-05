package features.snack4.composite;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.json.JsonType;

/**
 * @author noear 2023/1/19 created
 */
public class DomTest {
    @Test
    public void ary(){
        ONode oNode = new ONode();
        oNode.addNew().newObject();

        String json = oNode.toJson();

        System.out.println(json);

        assert ONode.fromJson(json).get(1).isObject();
    }

    @Test
    public void obj(){
        ONode oNode = new ONode();
        oNode.getOrNew("n1").newObject();

        String json = oNode.toJson();

        System.out.println(json);

        assert ONode.fromJson(json).get("n1").isObject();
    }
}
