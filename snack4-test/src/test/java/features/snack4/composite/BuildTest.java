package features.snack4.composite;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author noear 2023/7/31 created
 */
public class BuildTest {
    @Test
    public void build() {
        Collection<String> alias_ary = Arrays.asList("a", "b");
        String text = "hello";

        ONode data = new ONode().then((d) -> {
            d.getOrNew("platform").setValue("all");

            d.getOrNew("audience").getOrNew("alias").addAll(alias_ary);

            d.getOrNew("options")
                    .set("apns_production", false);

            d.getOrNew("notification").then(n -> {
                n.getOrNew("ios")
                        .set("alert", text)
                        .set("badge", 0)
                        .set("sound", "happy");
            });
        });

        String message = data.toJson();

        assert message != null && message.length() > 0;
        System.out.println(message);
        assert message.contains("notification");
        assert message.contains("hello");
    }
}
