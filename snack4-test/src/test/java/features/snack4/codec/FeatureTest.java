package features.snack4.codec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.json.JsonParseException;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Test
    public void Read_UnwrapJsonString() {
        assert ONode.ofJson("{user_info:'{a:1,b:2}'}", Feature.Read_ConvertSnakeToCamel, Feature.Read_UnwrapJsonString)
                .get("userInfo").isObject();

        assert ONode.ofJson("{user_info:'{a:1,b:2}'}")
                .get("user_info").isString();
    }

    @Test
    public void Read_AllowBackslashEscapingAnyCharacter() {
        String json0 = "{'a':'\\a'}";

        String json = ONode.ofJson(json0, Feature.Read_AllowBackslashEscapingAnyCharacter).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"a\":\"\\\\a\"}", json);

        Assertions.assertThrows(JsonParseException.class, () -> {
            ONode.ofJson(json0);
        });
    }

    @Test
    public void Read_AllowInvalidEscapeCharacter() {
        String json0 = "{'a':'\\a'}";

        String json = ONode.ofJson(json0, Feature.Read_AllowInvalidEscapeCharacter).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"a\":\"a\"}", json);

        Assertions.assertThrows(JsonParseException.class, () -> {
            ONode.ofJson(json0);
        });
    }

    @Test
    public void Read_AllowUnescapedControlCharacters() {
        String json0 = "{'a':'\1'}";

        String json = ONode.ofJson(json0, Feature.Read_AllowUnescapedControlCharacters).toJson();
        System.out.println(json);

        Assertions.assertThrows(JsonParseException.class, () -> {
            ONode.ofJson(json0);
        });
    }

    @Test
    public void Read_UseBigNumberMode() {
        String json = "{'a':1}";

        Map data = ONode.ofJson(json).toBean();
        System.out.println(data.get("a").getClass());
        Assertions.assertEquals(Integer.class, data.get("a").getClass());

        data = ONode.ofJson(json, Feature.Read_UseBigNumberMode).toBean();
        System.out.println(data.get("a").getClass());
        Assertions.assertEquals(BigInteger.class, data.get("a").getClass());
    }

    @Test
    public void Read_AllowUseGetter() {
        NumberBean bean = new NumberBean();

        String json = ONode.ofBean(bean).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"a\":1,\"b\":2,\"c\":3.0,\"d\":4.0}", json);

        json = ONode.ofBean(bean, Feature.Read_AllowUseGetter).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"a\":101,\"b\":2,\"c\":3.0,\"d\":4.0}", json);
    }

    @Test
    public void Read_OnlyUseGetter() {
        NumberBean bean = new NumberBean();

        String json = ONode.ofBean(bean).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"a\":1,\"b\":2,\"c\":3.0,\"d\":4.0}", json);

        json = ONode.ofBean(bean, Feature.Read_OnlyUseGetter).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"a\":101}", json);
    }

    @Test
    public void Read_AutoType() {
        Map<String, Object> data = new HashMap<>();
        data.put("user", new NumberBean());

        String json = ONode.ofBean(data, Feature.Write_ClassName).toJson();

        data = ONode.deserialize(json, Map.class);
        System.out.println(data);
        Assertions.assertEquals("{@type=java.util.HashMap, user={@type=features.snack4.codec.FeatureTest$NumberBean, a=1, b=2, c=3.0, d=4.0}}", data.toString());

        data = ONode.deserialize(json, Map.class, Feature.Read_AutoType);
        System.out.println(data);
        Assertions.assertEquals("{user=NumberBean{a=1, b=2, c=3.0, d=4.0}}", data.toString());
    }

    @Test
    public void Write_FailOnUnknownProperties() {
        String json = "{name:'aaa'}";

        ONode.deserialize(json, NumberBean.class);

        Assertions.assertThrows(Throwable.class, () -> {
            ONode.deserialize(json, NumberBean.class, Feature.Write_OnlyUseSetter, Feature.Write_FailOnUnknownProperties);
        });
    }

    @Test
    public void Write_UnquotedFieldNames() {
        NumberBean bean = new NumberBean();

        String json = ONode.ofBean(bean).toJson();
        System.out.println(json);

        json = ONode.ofBean(bean, Feature.Write_UnquotedFieldNames).toJson();
        System.out.println(json);
    }

    @Test
    public void Write_Nulls() {
        NullBean bean = new NullBean();

        String json = ONode.ofBean(bean).toJson();
        System.out.println(json);
        Assertions.assertEquals("{}", json);

        json = ONode.ofBean(bean, Feature.Write_Nulls).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"name\":null,\"age\":null,\"items\":null,\"isMan\":null}", json);
    }

    @Test
    public void Write_NullListAsEmpty() {
        NullBean bean = new NullBean();

        String json = ONode.ofBean(bean).toJson();
        System.out.println(json);
        Assertions.assertEquals("{}", json);

        json = ONode.ofBean(bean, Feature.Write_Nulls, Feature.Write_NullListAsEmpty).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"name\":null,\"age\":null,\"items\":[],\"isMan\":null}", json);
    }

    @Test
    public void Write_NullStringAsEmpty() {
        NullBean bean = new NullBean();

        String json = ONode.ofBean(bean).toJson();
        System.out.println(json);
        Assertions.assertEquals("{}", json);

        json = ONode.ofBean(bean, Feature.Write_Nulls, Feature.Write_NullStringAsEmpty).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"name\":\"\",\"age\":null,\"items\":null,\"isMan\":null}", json);
    }

    @Test
    public void Write_NullBooleanAsFalse() {
        NullBean bean = new NullBean();

        String json = ONode.ofBean(bean).toJson();
        System.out.println(json);
        Assertions.assertEquals("{}", json);

        json = ONode.ofBean(bean, Feature.Write_Nulls, Feature.Write_NullBooleanAsFalse).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"name\":null,\"age\":null,\"items\":null,\"isMan\":false}", json);
    }

    @Test
    public void Write_NullNumberAsZero() {
        NullBean bean = new NullBean();

        String json = ONode.ofBean(bean).toJson();
        System.out.println(json);
        Assertions.assertEquals("{}", json);

        json = ONode.ofBean(bean, Feature.Write_Nulls, Feature.Write_NullNumberAsZero).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"name\":null,\"age\":0,\"items\":null,\"isMan\":null}", json);
    }

    @Test
    public void Write_AllowUseSetter() {
        String json0 = "{\"a\":11,\"b\":12,\"c\":13.0,\"d\":14.0}";

        NumberBean bean = ONode.ofJson(json0).toBean(NumberBean.class);
        System.out.println(bean);
        Assertions.assertEquals("NumberBean{a=11, b=12, c=13.0, d=14.0}", bean.toString());

        bean = ONode.ofJson(json0, Feature.Write_AllowUseSetter).toBean(NumberBean.class);
        System.out.println(bean);
        Assertions.assertEquals("NumberBean{a=111, b=12, c=13.0, d=14.0}", bean.toString());
    }

    @Test
    public void Write_OnlyUseOnlySetter() {
        String json0 = "{\"a\":11,\"b\":12,\"c\":13.0,\"d\":14.0}";

        NumberBean bean = ONode.ofJson(json0).toBean(NumberBean.class);
        System.out.println(bean);
        Assertions.assertEquals("NumberBean{a=11, b=12, c=13.0, d=14.0}", bean.toString());

        bean = ONode.ofJson(json0, Feature.Write_OnlyUseSetter).toBean(NumberBean.class);
        System.out.println(bean);
        Assertions.assertEquals("NumberBean{a=111, b=2, c=3.0, d=4.0}", bean.toString());
    }

    @Test
    public void Write_PrettyFormat() {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", "a");

        String json = ONode.ofBean(data).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"userId\":\"a\"}", json);

        json = ONode.ofBean(data, Feature.Write_PrettyFormat).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\n" +
                "  \"userId\": \"a\"\n" +
                "}", json);
    }

    @Test
    public void Write_UseSingleQuotes() {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", "a");

        String json = ONode.ofBean(data).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"userId\":\"a\"}", json);

        json = ONode.ofBean(data, Feature.Write_UseSingleQuotes).toJson();
        System.out.println(json);
        Assertions.assertEquals("{'userId':'a'}", json);
    }

    @Test
    public void Write_UseSnakeStyle() {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", 1);

        String json = ONode.ofBean(data).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"userId\":1}", json);

        json = ONode.ofBean(data, Feature.Write_UseSnakeStyle).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"user_id\":1}", json);
    }

    @Test
    public void Write_EnumUsing() {
        Map<String, Object> data = new HashMap<>();
        data.put("a", Membership.Level2);

        String json = ONode.ofBean(data).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"a\":1}", json);

        json = ONode.ofBean(data, Feature.Write_EnumUsingName).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"a\":\"Level2\"}", json);

        json = ONode.ofBean(data, Feature.Write_EnumUsingToString).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"a\":\"Membership-1\"}", json);
    }

    @Test
    public void Write_ClassName() {
        Map<String, Object> data = new HashMap<>();
        data.put("a", new DateBean());

        String json = ONode.ofBean(data, Feature.Write_ClassName).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"@type\":\"java.util.HashMap\",\"a\":{\"@type\":\"features.snack4.codec.FeatureTest$DateBean\",\"date\":1760073353199}}", json);

        json = ONode.ofBean(data, Feature.Write_ClassName, Feature.Write_NotRootClassName).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"a\":{\"@type\":\"features.snack4.codec.FeatureTest$DateBean\",\"date\":1760073353199}}", json);

        json = ONode.ofBean(data, Feature.Write_ClassName, Feature.Write_NotMapClassName).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"a\":{\"@type\":\"features.snack4.codec.FeatureTest$DateBean\",\"date\":1760073353199}}", json);
    }

    @Test
    public void Write_UseRawBackslash() {
        Map<String, Object> data = new HashMap<>();
        data.put("a", "\\1");

        String json = ONode.ofBean(data).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"a\":\"\\\\1\"}", json);

        json = ONode.ofBean(data, Feature.Write_UseRawBackslash).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"a\":\"\\1\"}", json);
    }

    @Test
    public void Write_BrowserCompatible() {
        String json = ONode.ofJson("{a:'中国'}", Feature.Write_BrowserCompatible).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"a\":\"\\u4e2d\\u56fd\"}", json);
    }

    @Test
    public void Write_UseDateFormat() {
        DateBean bean = new DateBean();
        String json = ONode.ofBean(bean, Options.of(Feature.Write_UseDateFormat).dateFormat("yyyy-MM-dd")).toJson();
        System.out.println(json);
        Assertions.assertTrue(json.contains("-"));
    }

    @Test
    public void Write_NumbersAsString() {
        NumberBean bean = new NumberBean();
        String json = ONode.ofBean(bean, Feature.Write_NumbersAsString).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"a\":\"1\",\"b\":\"2\",\"c\":\"3.0\",\"d\":\"4.0\"}", json);
    }

    @Test
    public void Write_BigNumbersAsString() {
        NumberBean bean = new NumberBean();
        String json = ONode.ofBean(bean, Feature.Write_BigNumbersAsString).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"a\":1,\"b\":\"2\",\"c\":3.0,\"d\":\"4.0\"}", json);
    }

    @Test
    public void Write_NumberTypeSuffix() {
        NumberBean bean = new NumberBean();
        String json = ONode.ofBean(bean, Feature.Write_NumberTypeSuffix).toJson();
        System.out.println(json);
        Assertions.assertEquals("{\"a\":1,\"b\":2L,\"c\":3.0F,\"d\":4.0D}", json);
    }

    static class NumberBean {
        private int a = 1;
        private long b = 2;
        private float c = 3;
        private double d = 4;

        public int getA() {
            return a + 100;
        }

        public void setA(int a) {
            this.a = a + 100;
        }

        @Override
        public String toString() {
            return "NumberBean{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    ", d=" + d +
                    '}';
        }
    }

    static class DateBean {
        private Date date = new Date(1760073353199L);
    }

    static class NullBean {
        String name;
        Integer age;
        List items;
        Boolean isMan;
    }

    static enum Membership {
        Level1, Level2, Level3;

        @Override
        public String toString() {
            return "Membership-" + ordinal();
        }
    }
}