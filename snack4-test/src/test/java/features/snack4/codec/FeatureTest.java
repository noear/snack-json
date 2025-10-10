package features.snack4.codec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.json.JsonParseException;

/**
 *
 * @author noear 2025/10/10 created
 *
 */
public class FeatureTest {
    @Test
    public void Read_AllowComment() {
        ONode.ofJson("{} //ddd", Feature.Read_AllowComment);

        Assertions.assertThrows(JsonParseException.class, () -> {
            ONode.ofJson("{} //ddd");
        });
    }

    @Test
    public void Read_DisableSingleQuotes() {
        ONode.ofJson("{'a':1}");

        Assertions.assertThrows(JsonParseException.class, () -> {
            ONode.ofJson("{'a':1}", Feature.Read_DisableSingleQuotes);
        });
    }

    @Test
    public void Read_DisableUnquotedKeys() {
        ONode.ofJson("{a:1}");

        Assertions.assertThrows(JsonParseException.class, () -> {
            ONode.ofJson("{a:1}", Feature.Read_DisableUnquotedKeys);
        });
    }

    @Test
    public void Read_AllowEmptyKeys() {
        ONode.ofJson("{:1}", Feature.Read_AllowEmptyKeys);

        Assertions.assertThrows(JsonParseException.class, () -> {
            ONode.ofJson("{:1}");
        });
    }

    @Test
    public void Read_AllowZeroLeadingNumbers() {
        ONode.ofJson("{a:01}", Feature.Read_AllowZeroLeadingNumbers);

        Assertions.assertThrows(JsonParseException.class, () -> {
            ONode.ofJson("{a:01}");
        });
    }

    @Test
    public void Read_ConvertSnakeToCamel() {
        assert ONode.ofJson("{user_info:'1'}", Feature.Read_ConvertSnakeToCamel)
                .get("userInfo").isString();

        assert ONode.ofJson("{user_info:'1'}")
                .get("userInfo").isNull();
    }
}