package features.snack4.composite;

import demo.snack4._model5.TypeAImpl;
import demo.snack4._model5.TypeBImpl;
import demo.snack4._model5.TypeC;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Feature;
import org.noear.snack4.Options;

/**
 * @author noear 2025/5/9 created
 */
public class ClassType {
    @Test
    public void case1() {
        TypeC c = new TypeC();
        c.typeA = new TypeAImpl();
        c.typeB = new TypeBImpl();

        String rst = ONode.serialize(c, Feature.Write_ClassName);
        System.out.println(rst);

        assert "{\"@type\":\"demo.snack4._model5.TypeC\",\"typeA\":{\"@type\":\"demo.snack4._model5.TypeAImpl\"},\"typeB\":{\"@type\":\"demo.snack4._model5.TypeBImpl\"}}".equals(rst);
    }

    @Test
    public void case2() {
        TypeC c = new TypeC();
        c.typeA = new TypeAImpl();
        c.typeB = new TypeBImpl();

        String rst = ONode.from(c, Feature.Write_ClassName, Feature.Write_NotRootClassName).toJson();
        System.out.println(rst);

        assert "{\"typeA\":{\"@type\":\"demo.snack4._model5.TypeAImpl\"},\"typeB\":{\"@type\":\"demo.snack4._model5.TypeBImpl\"}}".equals(rst);
    }
}
