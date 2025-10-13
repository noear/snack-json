package features.snack4.jsonpath.manual;

import org.junit.jupiter.api.Test;
import org.noear.snack4.jsonpath.filter.Term;

import java.util.function.Predicate;

/**
 *
 * @author noear 2025/10/13 created
 *
 */
public class TermTest {
    @Test
    public void case1() {
        testTerm("match(@.b, '[jk]')", o -> o.getOp() == null);
        testTerm("size(@.b) > 5", o -> o.getOp().equals(">"));
        testTerm("!match(@.name, '^J.*')", o -> o.isNot() && o.getOp() == null);
        testTerm("!(match(@.name, '^J.*'))", o -> o.isNot() && o.getOp() == null);
        testTerm("(size(@.items) > 3)", o -> o.getOp().equals(">") && o.getRight().getValue().equals("3"));
        testTerm("$.absent1 == $.absent2", o -> o.getOp().equals("=="));
        testTerm("1 <= $.arr", o -> o.getOp().equals("<="));
        testTerm("$.a > 12", o -> o.getOp().equals(">"));

        testTerm("@.a like '1'", o -> o.getOp().equals("like"));

        //testTerm("@.a not like '1'", o -> o.getOp().equals("not like"));
        //testTerm("@.a notLike '1'", o -> o.getOp().equals("notLike"));

        //testTerm("@.a starts with '1'", o -> o.getOp().equals("starts with"));
        //testTerm("@.a startsWith '1'", o -> o.getOp().equals("startsWith"));
        //testTerm("@.columnCode == \"#YEAR#YTD#Period#Actual#ACCOUNT#[ICP None]#[None]#BB#REPORT#PRCTotal\"", o -> o.getOp().equals("=="));
    }

    public void testTerm(String token, Predicate<Term> predicate) {
        Term o = Term.get(token);

        if (predicate.test(o) == false) {
            assert false;
        }
    }
}
