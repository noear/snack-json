package features.snack4.v3_composite;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.node.Feature;

/**
 * @author noear 2022/12/15 created
 */
public class NullTest {
    @Test
    public void test1() {
        String json = "{num:null}";
        ONode node = ONode.ofJson(json, Feature.Write_Nulls);

        System.out.println(node.toJson());
        assert node.get("num").isNull();

        Object tmp = node.toBean();
        System.out.println(tmp);
    }
}
