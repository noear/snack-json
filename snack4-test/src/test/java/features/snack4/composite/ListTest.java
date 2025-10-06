package features.snack4.composite;

import demo.snack4._models.MyList;
import demo.snack4._models.NumberModel;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.codec.TypeRef;

/**
 * @author noear 2023/8/17 created
 */
public class ListTest {
    @Test
    public void test1() {
        String json = "[{}]";

        //自定义 list 接口
        MyList<NumberModel> list = ONode.load(json).to(new TypeRef<MyList<NumberModel>>() {
        });

        assert list != null;
        assert list.size() == 1;
        assert list.get(0) instanceof NumberModel;
    }
}
