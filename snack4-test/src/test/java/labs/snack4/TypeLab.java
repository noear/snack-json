package labs.snack4;

import org.junit.jupiter.api.Test;

/**
 *
 * @author noear 2025/10/4 created
 *
 */
public class TypeLab {
    @Test
    public void case11() {
        assert Double.class == Double.TYPE; //no
    }

    @Test
    public void case12() {
        assert Double.class.equals(Double.TYPE); //no
    }

    @Test
    public void case21() {
        assert Number.class.isAssignableFrom(Double.class);//ok
    }

    @Test
    public void case22() {
        assert Number.class.isAssignableFrom(Double.TYPE);//no
    }

    @Test
    public void case31() {
        double d = Double.NEGATIVE_INFINITY;
        assert Number.class.isInstance(d); //ok
    }

    @Test
    public void case32() {
        Double d = Double.NEGATIVE_INFINITY;
        assert Number.class.isInstance(d); //ok
    }
}
