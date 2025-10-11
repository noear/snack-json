package features.snack4.path.manual;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.jsonpath.segment.SelectSegment;

/**
 *
 * @author noear 2025/10/11 created
 *
 */
public class SplitSelectorsTest2 {
    @Test
    public void case1() {
        Assertions.assertEquals(2, SelectSegment.splitSelectors("1, 1").size());
        Assertions.assertEquals(4, SelectSegment.splitSelectors("1, 5, 10:20, 30").size());
    }
}
