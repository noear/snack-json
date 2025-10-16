package features.snack4.jsonpath.RFC9535;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONAware;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 *
 * @author noear 2025/10/16 created
 *
 */
public class RFC9535_s2130 extends AbsRFC9535{
    static String json = "{\"a\":[{\"b\":0},{\"b\":1},{\"c\":2}]}";
    @Test
    public void case1() {
        queryCompatibleDo("$.a[*].b","[0,1]");
    }

    private ONode queryCompatibleDo(String expr, String ref) {
        ONode oNode = ofJson(json).select(expr);
        String rst1 = oNode.toJson();
        System.out.println("::" + expr);
        System.out.println(rst1);

        JSONAware jsonAware = JsonPath.read(json, expr);
        String rst2 = jsonAware.toJSONString();
        System.out.println(rst2);

        assert rst2.equals(rst1);
        assert rst1.equals(ref);

        return oNode;
    }
}
