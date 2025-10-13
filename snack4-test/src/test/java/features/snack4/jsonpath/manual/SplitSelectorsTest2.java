package features.snack4.jsonpath.manual;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.jsonpath.util.SelectUtil;

/**
 *
 * @author noear 2025/10/11 created
 *
 */
public class SplitSelectorsTest2 {
    @Test
    public void case1() {
        Assertions.assertEquals(2, SelectUtil.splitSelectors("1, 1").size());
        Assertions.assertEquals(4, SelectUtil.splitSelectors("1, 5, 10:20, 30").size());
    }
}
