package features.snack4.composite;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.solon.core.handle.Result;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2022/3/31 created
 */
public class ResultNodeLongTest {
    @Test
    public void test() {
        ONode oNode = new ONode();
        oNode.set("num", 12L);
        Result result = Result.succeed(oNode);

        Options options = Options.of();
        options.addEncoder(Long.class, (opts, attr, value) ->  new ONode(String.valueOf(value)));

        ONode oNode2 =  ONode.from(result, options);
        String json = oNode2.serialize();
        System.out.println(json);
        assert json.contains("\"12\"") == false;
    }

    @Test
    public void test2() {
        Map<String,Object> oNode = new HashMap<>();
        oNode.put("num", 12L);
        Result result = Result.succeed(oNode);

        Options options = Options.of();
        options.addEncoder(Long.class, (opts, attr, value) -> new ONode(String.valueOf(value)));

        ONode oNode2 =  ONode.from(result, options);
        String json = oNode2.serialize();
        System.out.println(json);
        assert json.contains("\"12\"");
    }
}
