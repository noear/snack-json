package features.snack4.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.json.JsonProvider;

import java.io.StringReader;

/**
 *
 * @author noear 2025/10/13 created
 *
 */
public class WarnTest {
    @Test
    public void case1() {
        JsonProvider jsonProvider = () -> "";

        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            jsonProvider.read("", null);
        });

        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            jsonProvider.read(new StringReader(""), null);
        });

        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            jsonProvider.write(null, null);
        });

        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            jsonProvider.write(null, null, null);
        });
    }
}
