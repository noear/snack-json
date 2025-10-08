package features.snack4.composite;

import demo.snack4._model3.BSProps;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Feature;

/**
 * @author noear 2022/5/22 created
 */
public class FinalTest {
    @Test
    public void test() throws Exception{
//        String json = "{'bean-searcher':{'sql':{'dialect':'Oracle'}}}";
        String json = "{'sql':{'dialect':'Oracle'}}";
        BSProps bsProps = ONode.ofJson(json, Feature.Write_UseSetter).toBean(BSProps.class);

        assert "Oracle".equals(bsProps.getSql().getDialect());
    }
}
