package features.snack4.jsonpath.manual;

import org.junit.jupiter.api.Assertions;
import org.noear.snack4.ONode;

/**
 *
 * @author noear 2025/10/12 created
 *
 */
public abstract class AbsQueryTest {
    public ONode select(ONode node, String jsonpath) {
        System.out.println(":::" + jsonpath);
        ONode t1 = node.select(jsonpath);
        System.out.println(t1.toJson());


        return t1;
    }

    public void selectAssertSize(ONode node, int size, String jsonpath) {
        System.out.println("-------------: " + jsonpath);
        ONode t1 = node.select(jsonpath);
        System.out.println(t1.toJson());

        if (t1.size() != size) {
            System.out.println(node.toJson());
            Assertions.assertEquals(size, t1.size());
        }
    }
}
