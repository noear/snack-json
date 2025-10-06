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
        ONode node = ONode.load(json);

        System.out.println(node.serialize());
        assert node.get("num").isNull();

        Object tmp = node.to(Feature.Write_SerializeNulls);
        System.out.println(tmp);
    }
}
