package features.snack4.v3_composite;

import demo.snack4._model3.Server;
import demo.snack4._models.DateModel2;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 * @author noear 2021/12/31 created
 */
public class MemberTypeTest {
    @Test
    public void test1(){
        String json = "{\"id\":1, \"name\":\"n\"}";
        Server.One one = ONode.deserialize(json, Server.One.class);

        assert one != null;
    }

    @Test
    public void date1(){
        String json = "{date1:''}";

        DateModel2 model2 = ONode.deserialize(json, DateModel2.class);

        assert model2.date1 == null;
    }
}
