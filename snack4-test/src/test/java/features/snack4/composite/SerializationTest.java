package features.snack4.composite;

import com.alibaba.fastjson.JSON;
import demo.snack4._models.*;
import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.solon.core.util.ResourceUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class SerializationTest {

    @Test
    public void test0() throws Exception {
        String temp = ONode.serialize("aaa", Feature.Write_ClassName);
        assert "\"aaa\"".equals(temp);

        temp = ONode.serialize(12, Feature.Write_ClassName);
        assert "12".equals(temp);

        temp = ONode.serialize(true, Feature.Write_ClassName);
        assert "true".equals(temp);

        temp = ONode.serialize((Object) null, Feature.Write_ClassName);
        assert "null".equals(temp);

        temp = ONode.serialize(new Date(),  Feature.Write_ClassName);
        assert "null".equals(temp) == false;


        String tm2 = "{a:'http:\\/\\/raas.dev.zmapi.cn'}";

        ONode tm3 = ONode.load(tm2);

        tm3.toJson().equals("{\"a\":\"http://raas.dev.zmapi.cn\"}");
    }

    @Test
    public void test1() throws Exception {
        try {
            String val = null;
            val.equals("");
        } catch (Exception ex) {
            ex.printStackTrace();

            String json = ONode.serialize(ex, Feature.Write_ClassName);

            System.out.println(json);

            NullPointerException ex2 = ONode.deserialize(json, NullPointerException.class);

            Object ex22 = ONode.deserialize(json, Object.class);
            assert ex22 instanceof NullPointerException;


            Object ex23 = ONode.deserialize(json, Object.class);
            assert ex23 instanceof Map;

            ex2.printStackTrace();

            assert json != null;
        }
    }

    @Test
    public void test2() throws Exception {

        UserGroupModel group = new UserGroupModel();
        group.id = 9999;
        group.users = new ArrayList<>();
        group.users2 = new LinkedHashMap<>();
        group.users3 = new TreeSet<>();
        group.names = new String[5];
        group.ids = new short[5];
        group.iids = new Integer[5];
        group.dd = new BigDecimal(12);
        group.tt1 = new Timestamp(new Date().getTime());
        group.tt2 = new Date();

        for (short i = 0; i < 5; i++) {
            UserModel user = new UserModel();
            user.id = i;
            user.name = "张三" + i;
            user.note = null;
            group.users.add(user);
            group.users2.put(Integer.valueOf(i), user);
            group.names[i] = "李四" + i;
            group.ids[i] = i;
        }

        String json = ONode.serialize(group, Feature.Write_ClassName);
        System.out.println(json);
        UserGroupModel group2 = ONode.deserialize(json, UserGroupModel.class);

        Object group22 = ONode.deserialize(json, Object.class);
        assert group22 instanceof UserGroupModel;

        Object group23 = ONode.deserialize(json, Object.class);
        assert group23 instanceof Map;

        assert group2.id == 9999;

    }

    @Test
    public void test2_2() throws Exception {

        UserGroupModel group = new UserGroupModel();
        group.id = 9999;
        group.users = new ArrayList<>();
        group.users2 = new LinkedHashMap<>();
        group.users3 = new TreeSet<>();
        group.names = new String[5];
        group.ids = new short[5];
        group.iids = new Integer[5];
        group.dd = new BigDecimal(12);
        group.tt1 = new Timestamp(new Date().getTime());
        group.tt2 = new Date();

        for (short i = 0; i < 5; i++) {
            UserModel user = new UserModel();
            user.id = i;
            user.name = "张三" + i;
            user.note = null;
            group.users.add(user);
            group.users2.put(Integer.valueOf(i), user);
            group.names[i] = "李四" + i;
            group.ids[i] = i;
        }

        String json = ONode.serialize(group);//产生的json，没有@type
        System.out.println(json);
        UserGroupModel group2 = ONode.deserialize(json, UserGroupModel.class);

        Object group22 = ONode.deserialize(json, (new UserGroupModel() {
        }).getClass());
        assert group22 instanceof UserGroupModel;

        Object group23 = ONode.deserialize(json, LinkedHashMap.class);
        assert group23 instanceof Map;

        Object group24 = ONode.deserialize(json, Object.class);
        assert group24 instanceof Map;

        assert group2.id == 9999;

    }

    @Test
    public void test3() throws Exception {

        Map<String, Object> objx = new HashMap<>();
        Map<String, Object> obj = new LinkedHashMap<String, Object>();

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("a", 1);
        m.put("b", true);
        m.put("c", 1.2);
        m.put("d", new Date());

        list.add(m);

        obj.put("list", list);


        String json = ONode.serialize(obj, Feature.Write_ClassName);
        System.out.println(json);
        Map<String, Object> obj2 = ONode.deserialize(json, LinkedHashMap.class);
        assert obj2 instanceof LinkedHashMap;

        Map<String, Object> obj22 = ONode.deserialize(json, Object.class);
        assert obj22 instanceof HashMap;

        Map<String, Object> obj23 = ONode.deserialize(json, Object.class);
        assert obj23 instanceof Map;

        assert obj2.size() == 1;
    }


    @Test
    public void test4() throws Exception {
        UserModel user = new UserModel();
        user.id = 1111;
        user.name = "张三";
        user.note = null;

        OrderModel order = new OrderModel();
        order.user = user;
        order.order_id = 2222;
        order.order_num = "ddddd";


        String json = ONode.serialize(order, Feature.Write_ClassName);
        System.out.println(json);
        OrderModel order2 = ONode.deserialize(json, OrderModel.class);
        Object order22 = ONode.deserialize(json, Object.class);
        Map order23 = ONode.deserialize(json, Object.class);


        assert 1111 == order2.user.id;
    }

    @Test
    public void test5() throws Exception {
        CModel obj = new CModel();

        String json = ONode.serialize(obj, Feature.Write_ClassName);
        System.out.println(json);

        CModel obj2 = ONode.deserialize(json, CModel.class);

        assert obj2.list == null;
    }

    @Test
    public void test52() throws Exception {
        CModel obj = new CModel();
        obj.init();

        String json = ONode.serialize(obj,  Feature.Write_ClassName);
        System.out.println(json);

        CModel obj2 = ONode.deserialize(json, CModel.class);

        assert obj2.list.size() == obj.list.size();
    }

    @Test
    public void test53() throws Exception {
        CModel obj = new CModel();
        obj.build();

        String json = ONode.serialize(obj, Feature.Write_ClassName);
        System.out.println(json);

        CModel obj2 = ONode.deserialize(json, CModel.class);

        assert obj2.list.size() == obj.list.size();
    }

    @Test
    public void test6() throws Exception {
        String tmp = "{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}";
        //1.加载json
        Object n = ONode.load(tmp);

        assert n instanceof Map;
        assert ((Map) n).size() == 3;
    }

    @Test
    public void test7() throws Exception {
        String json = ResourceUtil.getResourceAsString("ResultTree.json");
        ResultTree rst = ONode.deserialize(json, ResultTree.class);

        System.out.println(rst);
        assert rst != null;

        String json2 = ONode.serialize(rst);
        assert json2 != null;
    }

    @Test
    public void test8() {
        String json = ONode.serialize("好人");
        System.out.println(json);
        String str = ONode.deserialize(json, String.class);
        System.out.println(str);

        assert "好人".equals(str);
    }

    @Test
    public void test8_2() { //不支持
        String json = JSON.toJSONString("好人");
        System.out.println(json);
        String str = JSON.parseObject(json, String.class);
        System.out.println(str);

        assert "好人".equals(str);
    }

    @Test
    public void test9() {
        String json = ONode.serialize(new BigDecimal("0.1"),  Feature.Write_ClassName);
        System.out.println(json);
    }
}
