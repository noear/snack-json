package features.snack4.v3_composite;

import features.snack4.v3_composite.test5.A;
import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;

/**
 * @author noear 2023/5/10 created
 */
public class _test5 {
    @Test
    public void test() {
        String poc = "{\"@type\":\"features.snack4.v3_composite.test5.A\"," +
                "\"b\":{\"@type\":\"features.snack4.v3_composite.test5.B\",\"bList\":\"str1\"}}";
        System.out.println(poc);
        A o = ONode.ofJson(poc, Feature.Write_AllowParameterizedConstructor).toBean(A.class);

        System.out.println(o);

        System.out.println(o.getB().getbList().size());
        assert o.getB().getbList().size() == 1;
    }
}
