package features.snack4.composite;

import demo.snack4._models.*;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Feature;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

/**
 * 2019.01.30
 *
 * @author cjl
 */
public class ObjectTest {
    /**
     * 无限递归
     *
     * @throws IllegalAccessException
     */
    @Test
    public void test000() throws IllegalAccessException {
        AModel a = new AModel();
        BModel b = new BModel();

        a.b = b;
        b.a = a;

        ONode c = ONode.from(a);

        System.out.println(c.toJson());

        assert "{\"b\":{}}".equals(c.toJson());
    }

    @Test
    public void test1() throws Exception {

        UserModel user = new UserModel();
        user.id = 1111;
        user.name = "张三";
        user.note = null;

        OrderModel order = new OrderModel();
        order.user = user;
        order.order_id = 2222;
        order.order_num = "ddddd";

        ONode c = ONode.from(order);

        System.out.println(c.toJson());

        OrderModel order2 = c.toBean(OrderModel.class);

        assert 1111 == order2.user.id;
    }

    @Test
    public void test2() throws IllegalAccessException {
        UserGroupModel group = new UserGroupModel();
        group.id = 9999;
        group.users = new ArrayList<>();
        group.names = new String[5];
        group.ids = new short[5];
        group.iids = new Integer[5];

        for (short i = 0; i < 5; i++) {
            UserModel user = new UserModel();
            user.id = i;
            user.name = "张三" + i;
            user.note = null;
            group.users.add(user);
            group.names[i] = "李四" + i;
            group.ids[i] = i;
            group.iids[i] = (int) i;
        }

        ONode c = ONode.from(group);

        System.out.println(c.toJson());

        assert 1 == c.get("users").get(1).get("id").getInt();

        UserGroupModel g = c.toBean(UserGroupModel.class);

        assert g.id == 9999;
    }

    @Test
    public void test3() {
        List<Object> d = new ArrayList<>();
        final TypeVariable<? extends Class<? extends List>>[] typeParameters = d.getClass().getTypeParameters();
        for (TypeVariable<? extends Class<? extends List>> t : typeParameters) {
            System.out.println(t.getName());
        }
    }

    @Test
    public void test4() {
        String json = "{\"names\":null}";
        ONode oNode = ONode.load(json);
        A a = oNode.toBean(A.class);

        assert a.names == null;
        System.out.println(a);

        oNode = ONode.from(a, Feature.Write_Nulls);
        String json2 = oNode.toJson();
        System.out.println(json2);

        assert json.equals(json2);
    }

    @Data
    public static class A {
        private List<String> names;
    }
}