package features.snack4.composite;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import java.time.Duration;

/**
 * @author noear 2024/9/10 created
 */
public class DurationTest {
    @Test
    public void test1() {
        Duration duration = ONode.load("'6s'").to(Duration.class);
        System.out.println(duration);

        duration = ONode.load("'6m'").to(Duration.class);
        System.out.println(duration);

        duration = ONode.load("'6d'").to(Duration.class);
        System.out.println(duration);
    }

    @Test
    public void test3() {
        Duration duration = ONode.load("'PT6S'").to(Duration.class);
        System.out.println(duration);

        duration = ONode.load("'PT6M'").to(Duration.class);
        System.out.println(duration);

        duration = ONode.load("'PT6H'").to(Duration.class);
        System.out.println(duration);
    }
}
