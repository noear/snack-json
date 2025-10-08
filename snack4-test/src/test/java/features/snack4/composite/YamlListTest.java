package features.snack4.composite;

import demo.snack4._models.SwaggerInfo;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.solon.Utils;
import org.noear.solon.core.Props;

/**
 * @author noear 2022/5/3 created
 */
public class YamlListTest {
    @Test
    public void test() {
        Props props = new Props(Utils.loadProperties("app.yml"));
        SwaggerInfo swaggerInfo = ONode.ofBean(props.getProp("swagger")).toBean(SwaggerInfo.class);

        assert swaggerInfo.getResources() != null;
        assert swaggerInfo.getResources().size() == 2;

        assert "2.0".equals(swaggerInfo.getVersion());
    }
}
