package features.snack4.jsonpath;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.jsonpath.JsonPathProvider;
import org.noear.snack4.jsonpath.JsonPathProviderWarn;

/**
 *
 * @author noear 2025/10/13 created
 *
 */
public class WarnTest {
    @Test
    public void case1(){
        JsonPathProvider jsonPathProvider = new JsonPathProviderWarn();

        Assertions.assertThrows(UnsupportedOperationException.class,() -> {
            jsonPathProvider.select(null,null);
        });

        Assertions.assertThrows(UnsupportedOperationException.class,() -> {
            jsonPathProvider.create(null,null);
        });

        Assertions.assertThrows(UnsupportedOperationException.class,() -> {
            jsonPathProvider.delete(null,null);
        });
    }
}
