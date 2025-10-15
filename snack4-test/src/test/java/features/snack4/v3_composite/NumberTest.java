package features.snack4.v3_composite;

import demo.snack4._models.NumberModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Feature;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear 2021/4/21 created
 */
public class NumberTest {
    @Test
    public void test1() {
        String json = "{num:50123.12E25}";
        ONode node = ONode.ofJson(json);

        System.out.println(node.toJson());
        assert 50123.12E25 == node.get("num").getDouble();
    }

    @Test
    public void test2() {
        String json = "{num:5344.34234e3}";
        ONode node = ONode.ofJson(json);

        System.out.println(node.toJson());
        assert 5344.34234e3 == node.get("num").getDouble();
    }

    @Test
    public void test3() {
        String json = "{num:1.0485E+10}";
        ONode node = ONode.ofJson(json);

        System.out.println(node.toJson());
        assert 1.0485E+10 == node.get("num").getDouble();
    }

    @Test
    public void test4() {
        String json = "{num:1.0485E-10}";
        ONode node = ONode.ofJson(json);

        System.out.println(node.toJson());
        assert 1.0485E-10 == node.get("num").getDouble();
    }

    @Test
    public void test4_2() {
        System.out.println(0E-10);

        String json = "{num:0E-10}";
        ONode node = ONode.ofJson(json);

        System.out.println(node.toJson());
        assert 0E-10 == node.get("num").getDouble();
    }

    @Test
    public void test5() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("num", 1.0485E-10);
        map.put("num11", new BigDecimal("12.1234567891").toPlainString());
        map.put("num12", new BigDecimal("123456789112345678911234567891123456789112.1234567891"));
        map.put("num21", new BigInteger("12"));
        map.put("num22", new BigInteger("123456789112345678911234567891123456789112"));

        ONode node = ONode.ofBean(map);
        String json = node.toJson();

        System.out.println(json);
        assert 1.0485E-10 == node.get("num").getDouble();
        assert new BigInteger("123456789112345678911234567891123456789112").compareTo((BigInteger) node.get("num22").getValue()) == 0;

        ONode node2 = ONode.ofJson(json);
        assert json.equals(node2.toJson());
    }

    @Test
    public void test6() {
        NumberModel mod = new NumberModel();

        mod.setNum01(true);
        mod.setNum02(Byte.parseByte("12"));
        mod.setNum11(Short.parseShort("125"));
        mod.setNum12(1);
        mod.setNum13(1L);
        mod.setNum21(new BigInteger("12345678911234567891123456789112345678911244444444444444"));
        mod.setNum22(new BigDecimal("123456789112345678911234567891123456789112.1234567891"));

        String json = ONode.ofBean(mod).toJson();
        System.out.println(json);

        String json2 = ONode.ofBean(mod, Feature.Write_ClassName).toJson();
        System.out.println(json2);


        NumberModel obj1 = ONode.ofJson(json).toBean(NumberModel.class);
        NumberModel obj2 = ONode.ofJson(json2).toBean(NumberModel.class);

        System.out.println(obj1);
        System.out.println(obj2);

        assert obj1.isNum01() == obj2.isNum01();
        assert obj1.getNum02() == obj2.getNum02();
        assert obj1.getNum11() == obj2.getNum11();
        assert obj1.getNum12() == obj2.getNum12();
        assert obj1.getNum13() == obj2.getNum13();
        assert obj1.getNum14() == obj2.getNum14();
        assert obj1.getNum21().compareTo(obj1.getNum21()) == 0;
        assert obj1.getNum22().compareTo(obj1.getNum22()) == 0;
    }

    @Test
    public void test7() {
        String json = "{a:1}";
        Map map = ONode.ofJson(json).toBean(Map.class);

        assert map.get("a") instanceof Integer;
    }

    @Test
    public void test8() {
        Number number = 12.12D;

        assert number.intValue() == 12;
        assert number.longValue() == 12;
    }

    @Test
    public void test9() {
        String json = "{\"a\":0.0000}";
        String json2 = ONode.ofJson(json, Feature.Read_UseBigNumberMode).toJson();

        Assertions.assertEquals(json, json2);
    }


    @Test
    public void test10() {
        String json = "{num15_2:''}";
        NumberModel tmp1 = ONode.ofJson(json).toBean(NumberModel.class);
        Assertions.assertNull(tmp1.getNum15_2());


        json = "{num15_2:'1'}";
        tmp1 = ONode.ofJson(json).toBean(NumberModel.class);
        Assertions.assertEquals(1D, tmp1.getNum15_2());
    }
}
