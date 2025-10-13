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

        testTerm("@.a in [1,2,3]", "@.a", "in", "[1,2,3]");
        testTerm("@.a like '1'", "@.a", "like", "'1'");

        testTerm("@.columnCode == \"#YEAR#YTD#Period#Actual#ACCOUNT#[ICP None]#[None]#BB#REPORT#PRCTotal\"", "@.columnCode", "==", "\"#YEAR#YTD#Period#Actual#ACCOUNT#[ICP None]#[None]#BB#REPORT#PRCTotal\"");
    }

    @Test
    public void case2() {
        testTerm("@.a not like '1'", "@.a", "not like", "'1'");
    }

    public void testTerm(String token, String left, String op, String right) {
        System.out.println("------------: " + token);

        String[] result = TermUtil.resolve(token);

        Assertions.assertEquals(left, result[0]);
        Assertions.assertEquals(op, result[1]);
        Assertions.assertEquals(right, result[2]);
    }
}