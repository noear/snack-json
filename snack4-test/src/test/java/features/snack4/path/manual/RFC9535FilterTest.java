package features.snack4.path.manual;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.Expression;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.QueryMode;

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
        ONode node = ONode.ofJson(json1);
        boolean rst = Expression.get("$.absent1 == $.absent2").test(node, new QueryContext(node, QueryMode.SELECT));
        assertTrue(rst);
    }

    @Test
    public void case1_2() {
        ONode node = ONode.ofJson(json1);
        boolean rst = Expression.get("$.absent1 <= $.absent2").test(node, new QueryContext(node, QueryMode.SELECT));
        assertTrue(rst);
    }

    @Test
    public void case1_3() {
        ONode node = ONode.ofJson(json1);
        boolean rst = Expression.get("$.absent == 'g'").test(node, new QueryContext(node, QueryMode.SELECT));
        assertFalse(rst);
    }

    @Test
    public void case1_4() {
        ONode node = ONode.ofJson(json1);
        boolean rst = Expression.get("$.absent1 != $.absent2").test(node, new QueryContext(node, QueryMode.SELECT));
        assertFalse(rst);
    }

    @Test
    public void case1_5() {
        ONode node = ONode.ofJson(json1);
        boolean rst = Expression.get("$.absent != 'g'").test(node, new QueryContext(node, QueryMode.SELECT));
        assertTrue(rst);
    }

    @Test
    public void case1_6() {
        ONode node = ONode.ofJson(json1);
        boolean rst = Expression.get("1 <= 2").test(node, new QueryContext(node, QueryMode.SELECT));
        assertTrue(rst);
    }

    @Test
    public void case1_7() {
        ONode node = ONode.ofJson(json1);
        boolean rst = Expression.get("1 > 2").test(node, new QueryContext(node, QueryMode.SELECT));
        assertFalse(rst);
    }

    @Test
    public void case1_8() {
        ONode node = ONode.ofJson(json1);
        boolean rst = Expression.get("13 == '13'").test(node, new QueryContext(node, QueryMode.SELECT));
        assertFalse(rst);
    }

    @Test
    public void case1_9() {
        ONode node = ONode.ofJson(json1);
        boolean rst = Expression.get("'a' <= 'b'").test(node, new QueryContext(node, QueryMode.SELECT));
        assertTrue(rst);
    }

    @Test
    public void case1_10() {
        ONode node = ONode.ofJson(json1);
        boolean rst = Expression.get("'a' > 'b'").test(node, new QueryContext(node, QueryMode.SELECT));
        assertFalse(rst);
    }
}
