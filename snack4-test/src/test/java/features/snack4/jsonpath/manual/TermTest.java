package features.snack4.jsonpath.manual;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.jsonpath.util.TermUtil;

/**
 *
 * @author noear 2025/10/13 created
 *
 */
public class TermTest {
    @Test
    public void case1() {
        testTerm("match(@.b, '[jk]')", "match(@.b, '[jk]')", null, null);
        testTerm("append(@.b, {'a':'1'})", "append(@.b, {'a':'1'})", null, null);
        testTerm("size(@.b) > 5", "size(@.b)", ">", "5");
        testTerm("match(@.name, '^J.*')", "match(@.name, '^J.*')", null, null);
        testTerm("$.absent1 == $.absent2", "$.absent1", "==", "$.absent2");
        testTerm("1 <= $.arr", "1", "<=", "$.arr");
        testTerm("$.a > 12", "$.a", ">", "12");

        testTerm("@.columnCode == \"#YEAR#YTD#Period#Actual#ACCOUNT#[ICP None]#[None]#BB#REPORT#PRCTotal\"", "@.columnCode", "==", "\"#YEAR#YTD#Period#Actual#ACCOUNT#[ICP None]#[None]#BB#REPORT#PRCTotal\"");

        testTerm("@.a in [1,2,3]", "@.a", "in", "[1,2,3]");
        testTerm("@.a not in [1,2,3]", "@.a", "not in", "[1,2,3]");

        testTerm("@.a like '1'", "@.a", "like", "'1'");
        testTerm("@.a not like '1'", "@.a", "not like", "'1'");

        testTerm("@.a rlike '1'", "@.a", "rlike", "'1'");
        testTerm("@.a not rlike '1'", "@.a", "not rlike", "'1'");

        testTerm("@.a between '1'", "@.a", "between", "'1'");
        testTerm("@.a not between '1'", "@.a", "not between", "'1'");

        testTerm("@.a and '1'", "@.a", "and", "'1'");
        testTerm("@.a or '1'", "@.a", "or", "'1'");

        testTerm("@.a starts with '1'", "@.a", "starts with", "'1'");
        testTerm("@.a ends with '1'", "@.a", "ends with", "'1'");

        testTerm("@.a contains '1'", "@.a", "contains", "'1'");
        testTerm("@.a not contains '1'", "@.a", "not contains", "'1'");
    }

    public void testTerm(String token, String left, String op, String right) {
        System.out.println("------------: " + token);

        String[] result = TermUtil.resolve(token);

        Assertions.assertEquals(left, result[0]);
        Assertions.assertEquals(op, result[1]);
        Assertions.assertEquals(right, result[2]);
    }
}