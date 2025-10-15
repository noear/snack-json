package features.snack4.v3_composite;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.node.Feature;
import org.noear.snack4.node.Options;

/**
 * @author noear 2021/12/7 created
 */
public class FeatureTest {
    @Test
    public void test() {
        Options options = Options.of();
        ONode oNode = new ONode(options);

        System.out.println(options.getFeatures());
        assert options.hasFeature(Feature.Write_NullStringAsEmpty) == false;
        assert oNode.get("name").getString() == null;

        options.addFeatures(Feature.Write_NullStringAsEmpty);
        System.out.println(options.getFeatures());
        assert options.hasFeature(Feature.Write_NullStringAsEmpty);
        assert oNode.get("name").getString() != null;
    }
}
