package features.snack4.path.RFC9535;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.jsonpath.JsonPath;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author noear 2025/10/11 created
 *
 */
public class RFC9535_s2610 {
    @Test
    public void case1(){
        queryAssert("$.a", "null"); //对象值
        queryAssert("$.a[0]", "null"); //null用作数组
        queryAssert("$.a.d", "null"); //null用作对象
        queryAssert("$.b[0]", "null"); //数组值
        queryAssert("$.b[*]", "[null]"); //数组值
        queryAssert("$.b[?@]", "[]"); //存在
        queryAssert("$.b[?@==null]", "[]");//比较
        queryAssert("$.c[?@.d==null]", "[]"); //与“缺失”值的比较
        queryAssert("$.null", "1"); //null根本不是 JSON ，只是一个成员名称字符串
    }

    private void queryAssert(String expr, String expected) {
        JsonPath jsonPath = JsonPath.compile(expr);

        String actual = jsonPath.select(ONode.ofJson(json, Options.of().RFC9535(true))).toJson();
        String expected2 = ONode.ofJson(expected).toJson(); //重新格式化
        System.out.println("::" + expr);
        assertEquals(expected2, actual);
    }

    static final String json = "{\"a\": null, \"b\": [null], \"c\": [{}], \"null\": 1}";
}
