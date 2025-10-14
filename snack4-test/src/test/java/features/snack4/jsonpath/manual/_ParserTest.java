package features.snack4.jsonpath.manual;


import org.junit.jupiter.api.Test;
import org.noear.snack4.jsonpath.JsonPathParser;

/**
 *
 * @author noear 2025/10/14 created
 *
 */
public class _ParserTest {
    @Test
    public void case1(){
        JsonPathParser.parse("$.store.book[?(@.category.concat(@.author) == 'referenceNigel Rees')]");
    }
}
