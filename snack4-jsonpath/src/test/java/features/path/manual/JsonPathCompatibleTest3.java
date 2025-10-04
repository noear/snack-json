package features.path.manual;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class JsonPathCompatibleTest3 {
    String json = "{\"request1\":{\"result\":[{\"relTickers\":[{\"tickerId\":1},{\"tickerId\":1.1}],\"accountId\":400006},{\"relTickers\":[{\"tickerId\":2},{\"tickerId\":2.2}]},{\"relTickers\":[{\"tickerId\":3}]},{\"relTickers\":[{\"tickerId\":4}]},{\"relTickers\":[{\"tickerId\":5}]},{\"relTickers\":[{\"tickerId\":6}]}]}}\n";

    @Test
    public void case1() {
        compatible_do("1", json, "$.request1.result[*].relTickers[*].tickerId.first()");
        compatible_do("6", json, "$.request1.result[*].relTickers[*].tickerId.last()");
    }

    @Test
    public void case2(){
        compatible_do("1", json, "$.request1.result[*].relTickers[*].first().tickerId");
        compatible_do("6", json, "$.request1.result[*].relTickers[*].last().tickerId");
    }

    private void compatible_do(String hint, String json, String jsonpathStr) {
        System.out.println("::::" + hint);

        ONode tmp = ONode.load(json).select(jsonpathStr);
        System.out.println(tmp.toJson());

        Object tmp2 = JsonPath.read(json, jsonpathStr);
        System.out.println(tmp2);

        assert tmp.toJson().equals(tmp2.toString());
    }
}
