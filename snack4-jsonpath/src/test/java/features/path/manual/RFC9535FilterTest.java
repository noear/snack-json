package features.path.manual;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.Expression;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author noear 2025/5/6 created
 */
public class RFC9535FilterTest {
    static final String json1 = "{\n" +
            "  \"obj\": {\"x\": \"y\"},\n" +
            "  \"arr\": [2, 3]\n" +
            "}";

    @Test
    public void case1_1() {
        ONode node = ONode.load(json1);
        boolean rst = Expression.get("$.absent1 == $.absent2").test(node, node);
        assertTrue(rst);
    }

    @Test
    public void case1_2() {
        ONode node = ONode.load(json1);
        boolean rst = Expression.get("$.absent1 <= $.absent2").test(node, node);
        assertTrue(rst);
    }

    @Test
    public void case1_3() {
        ONode node = ONode.load(json1);
        boolean rst = Expression.get("$.absent == 'g'").test(node, node);
        assertFalse(rst);
    }

    @Test
    public void case1_4() {
        ONode node = ONode.load(json1);
        boolean rst = Expression.get("$.absent1 != $.absent2").test(node, node);
        assertFalse(rst);
    }

    @Test
    public void case1_5() {
        ONode node = ONode.load(json1);
        boolean rst = Expression.get("$.absent != 'g'").test(node, node);
        assertTrue(rst);
    }

    @Test
    public void case1_6() {
        ONode node = ONode.load(json1);
        boolean rst = Expression.get("1 <= 2").test(node, node);
        assertTrue(rst);
    }

    @Test
    public void case1_7() {
        ONode node = ONode.load(json1);
        boolean rst = Expression.get("1 > 2").test(node, node);
        assertFalse(rst);
    }

    @Test
    public void case1_8() {
        ONode node = ONode.load(json1);
        boolean rst = Expression.get("13 == '13'").test(node, node);
        assertFalse(rst);
    }

    @Test
    public void case1_9() {
        ONode node = ONode.load(json1);
        boolean rst = Expression.get("'a' <= 'b'").test(node, node);
        assertTrue(rst);
    }

    @Test
    public void case1_10() {
        ONode node = ONode.load(json1);
        boolean rst = Expression.get("'a' > 'b'").test(node, node);
        assertFalse(rst);
    }
}
