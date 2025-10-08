package features.snack4.composite;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Feature;

/**
 * @author noear 2022/12/15 created
 */
public class NullTest {
    @Test
    public void test1() {
        String json = "{num:null}";
        ONode node = ONode.load(json, Feature.Write_Nulls);

        System.out.println(node.toJson());
        assert node.get("num").isNull();

        Object tmp = node.toBean();
        System.out.println(tmp);
    }
}
