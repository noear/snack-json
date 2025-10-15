package features.snack4.v3_composite;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.core.Feature;

import java.io.IOException;

/**
 * @author noear 2025/1/21 created
 */
public class EscapeTest {
    @Test
    public void case1() {
        String json = "{\"a\":\"\1\"}";

        ONode node = ONode.ofJson(json, Feature.Read_AllowUnescapedControlCharacters, Feature.Write_UseRawBackslash);
        String json2 = node.toJson();
        String json2Val = node.get("a").getString();
        String json2Val2 = node.get("a").toJson();

        System.out.println(node);
        System.out.println(json2);
        System.out.println(json2Val);
        System.out.println(json2Val2);

        JSONObject tmp = JSON.parseObject(json);
        String tmpJson = JSON.toJSONString(tmp);
        String tmpJsonVal = tmp.getString("a");

        System.out.println(tmp);
        System.out.println(tmpJson);
        System.out.println(tmpJsonVal);

        assert json2.equals(tmpJson);
        assert json2Val.equals(tmpJsonVal);
    }

    @Test
    public void case2() throws IOException {
        ONode c = ONode.ofJson("{\"a\":\" \\0\\1\\2\\3\\4\\5\\6\\7\"}", Feature.Read_AllowBackslashEscapingAnyCharacter);

        String a = c.get("a").getString();
        String a1 = " \0\1\2\3\4\5\6\7";
        assert a1.equals(a);

        assert "{\"a\":\" \\u0000\\u0001\\u0002\\u0003\\u0004\\u0005\\u0006\\u0007\"}".equals(c.toJson());
    }

    @Test
    public void case2_2() throws IOException {
        ONode c = ONode.ofJson("{\"a\":\" \\u0000\\u0001\\u0002\\u0003\\u0004\\u0005\\u0006\\u0007\"}");

        assert " \0\1\2\3\4\5\6\7".equals(c.get("a").getString());

        assert "{\"a\":\" \\u0000\\u0001\\u0002\\u0003\\u0004\\u0005\\u0006\\u0007\"}".equals(c.toJson());

    }

    @Test
    public void case3() throws IOException {
        ONode c = ONode.ofJson("{\"a\":\" \\u000f\\u0012\"}");

        assert " \u000f\u0012".equals(c.get("a").getString());

        assert "{\"a\":\" \\u000f\\u0012\"}".equalsIgnoreCase(c.toJson());
    }


    /**
     * 测试：unicode 转码
     */
    @Test
    public void case4() throws IOException {
        ONode c = ONode.ofJson("{\"a\":\"'\\u7684\\t\\n\"}");

        assert "'的\t\n".equals(c.get("a").getString());

        Assertions.assertEquals("{\"a\":\"'的\\t\\n\"}", c.toJson());
    }

    /**
     * 测试：emoji unicode 转码
     */
    @Test
    public void case5() throws IOException {
        ONode c = ONode.ofJson("{\"a\":\"'\\ud83d\\udc4c\\t\\n\"}", Feature.Write_BrowserCompatible);

        assert "'👌\t\n".equals(c.get("a").getString());

        Assertions.assertEquals("{\"a\":\"'\\ud83d\\udc4c\\t\\n\"}", c.toJson());
    }
}
