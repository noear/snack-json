package features.snack4.path.RFC9535;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.QueryContextImpl;
import org.noear.snack4.jsonpath.filter.Expression;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.QueryMode;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author noear 2025/5/6 created
 */
public class RFC9535_s2353_Comparison extends AbsRFC9535 {
    // SQL/JSON Path (ISO/IEC 9075)
    // IETF JSONPath (RFC 9535) https://www.rfc-editor.org/rfc/rfc9535.html

    static final String comparisonJson = "{\n" +
            "  \"obj\": {\"x\": \"y\"},\n" +
            "  \"arr\": [2, 3]\n" +
            "}";

    @Test
    public void comparisonTest() {
        //https://www.rfc-editor.org/rfc/rfc9535.html#filter-selector

        comparisonAssert("$.absent1 == $.absent2", true); //Empty nodelists
        comparisonAssert("$.absent1 <= $.absent2", true); //== implies <=
        comparisonAssert("$.absent == 'g'", false);
        comparisonAssert("$.absent1 != $.absent2", false);
        comparisonAssert("$.absent != 'g'", true);

        comparisonAssert("1 <= 2", true);
        comparisonAssert("1 > 2", false);
        comparisonAssert("13 == '13'", false);
        comparisonAssert("'a' <= 'b'", true);
        comparisonAssert("'a' > 'b'", false);

        comparisonAssert("$.obj == $.arr", false);
        comparisonAssert("$.obj != $.arr", true);
        comparisonAssert("$.obj == $.obj", true);
        comparisonAssert("$.obj != $.obj", false);
        comparisonAssert("$.arr == $.arr", true);
        comparisonAssert("$.arr != $.arr", false);

        comparisonAssert("$.obj == 17", false);
        comparisonAssert("$.obj != 17", true);

        comparisonAssert("$.obj <= $.arr", false);
        comparisonAssert("$.obj < $.arr", false);
        comparisonAssert("$.obj <= $.obj", true);
        comparisonAssert("$.arr <= $.arr", true);

        comparisonAssert("1 <= $.arr", false);
        comparisonAssert("1 >= $.arr", false);
        comparisonAssert("1 > $.arr", false);
        comparisonAssert("1 < $.arr", false);

        comparisonAssert("true <= true", true);
        comparisonAssert("true > true", false);
    }


    private void comparisonAssert(String expr, boolean expected) {
        System.out.println("----------------------: " + expr);
        ONode node = ofJson(comparisonJson);
        boolean actual = Expression.get(expr).test(node, new QueryContextImpl(node, QueryMode.SELECT));

        if (expected != actual) {
            System.out.println("::: " + actual + " != " + expected);
            System.out.println(comparisonJson);
            assert false;
        }
    }
}