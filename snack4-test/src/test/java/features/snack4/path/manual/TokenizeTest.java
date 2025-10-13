package features.snack4.path.manual;

import org.junit.jupiter.api.Test;
import org.noear.snack4.jsonpath.filter.Expression;

import java.util.List;

/**
 *
 * @author noear 2025/10/11 created
 *
 */
public class TokenizeTest {
    @Test
    public void case1() {
        // 测试函数调用
        testTokenize("match(@.b, '[jk]')", 1);
        testTokenize("size(@.b) > 5", 1);
        testTokenize("(match(@.name, '^J.*')) && (size(@.items) > 3)", 7);
        testTokenize("$.absent1 == $.absent2", 1);
        testTokenize("1 <= $.arr", 1);
        testTokenize("($.a > 12) || ($.b > 5)", 7);

        testTokenize("@.a like '1'", 1);
        testTokenize("@.a not like '1'", 1);
    }

    private void testTokenize(String expression, int size) {
        System.out.println("Expression: " + expression);
        List<Expression.Token> tokens = Expression.tokenize(expression);
        for (Expression.Token token : tokens) {
            System.out.println("  " + token);
        }
        assert tokens.size() == size;
    }
}
