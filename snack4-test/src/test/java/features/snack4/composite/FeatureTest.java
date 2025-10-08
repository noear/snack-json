package features.snack4.composite;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Feature;
import org.noear.snack4.Options;

/**
 * @author noear 2021/12/7 created
 */
public class FeatureTest {
    @Test
    public void test() {
        Options options = Options.of();
        ONode oNode = new ONode(options);

        System.out.println(options.getFeatures());
        assert options.hasFeature(Feature.Write_StringNullAsEmpty) == false;
        assert oNode.get("name").getString() == null;

        options.addFeature(Feature.Write_StringNullAsEmpty);
        System.out.println(options.getFeatures());
        assert options.hasFeature(Feature.Write_StringNullAsEmpty);
        assert oNode.get("name").getString() != null;
    }
}
