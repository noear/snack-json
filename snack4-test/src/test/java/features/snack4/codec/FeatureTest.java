package features.snack4.codec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.json.JsonParseException;

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
        throw new UnsupportedOperationException();
    }

    @Test
    public void Read_AllowInvalidEscapeCharacter() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void Read_AllowUnescapedControlCharacters() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void Read_UseBigNumberMode() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void Read_AllowUseGetter() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void Read_OnlyUseGetter() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void Read_AutoType() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void Write_FailOnUnknownProperties() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void Write_UnquotedFieldNames() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void Write_Nulls() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void Write_NullListAsEmpty() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void Write_NullStringAsEmpty() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void Write_NullBooleanAsFalse() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void Write_NullNumberAsZero() {
        NullBean bean = new NullBean();

        String json = ONode.ofBean(bean).toJson();
        System.out.println(json);

        json = ONode.ofBean(bean, Feature.Write_NullNumberAsZero).toJson();
        System.out.println(json);
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

        bean = ONode.ofJson(json0, Feature.Write_OnlyUseOnlySetter).toBean(NumberBean.class);
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
            return a;
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
    }

    static enum Membership {
        Level1, Level2, Level3;

        @Override
        public String toString() {
            return "Membership-" + ordinal();
        }
    }
}