package features.snack4.path.manual;

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
}
