package features.snack4.v3_composite;

import demo.snack4._models.UserModel;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.node.Feature;
import org.noear.snack4.node.DataType;

/**
 * @author noear 2022/9/7 created
 */
public class ExistsTest {
    @Test
    public void test1() {
        ONode oNode = new ONode();
        assert oNode.select("$.user").isNull();
        assert oNode.select("$.user").nodeType() == DataType.Undefined;

        ONode oNode2 = ONode.ofJson("{user:1}");
        assert oNode2.select("$.user").isNull() == false;
        assert oNode2.select("$.user").nodeType() != DataType.Null;

        ONode oNode3 = ONode.ofJson("{user:null}");
        assert oNode3.select("$.user").isNull();
        assert oNode3.select("$.user").nodeType() == DataType.Null;
    }

    @Test
    public void test2() {
        ONode oNode = new ONode();
        assert oNode.select("$.user").isNull();
        assert oNode.select("$.user").isUndefined();

        ONode oNode2 = ONode.ofJson("{user:1}");
        assert oNode2.select("$.user").isNull() == false;
        assert oNode2.select("$.user").isUndefined() == false;

        ONode oNode3 = ONode.ofJson("{user:null}");
        assert oNode3.select("$.user").isNull();
        assert oNode3.select("$.user").isUndefined() == false;
    }

    @Test
    public void test3() {
        ONode oNode4 = ONode.ofJson("[{user:null}]");
        assert oNode4.select("$[0].user").isNull();
        assert oNode4.select("$[0].user").isUndefined() == false;
        assert oNode4.exists("$[0].user");

        ONode oNode5 = ONode.ofJson("[{user:1}]");
        assert oNode5.select("$[0].user").isNull() == false;
        assert oNode5.select("$[0].user").isUndefined() == false;
        assert oNode5.exists("$[0].user");
        assert oNode5.select("$[0].user").getInt() == 1;
    }

    @Test
    public void test4() {
        ONode oNode4 = ONode.ofJson("[{user:null}]");
        assert oNode4.select("$[0].user.name").isNull();
        assert oNode4.select("$[0].user.name").isUndefined();
        assert oNode4.exists("$[0].user.name") == false;

        ONode oNode5 = ONode.ofJson("[{user:{}}]");
        assert oNode5.select("$[0].user.name").isNull();
        assert oNode5.select("$[0].user.name").isUndefined();
        assert oNode5.exists("$[0].user.name") == false;
    }

    @Test
    public void test5() {
        ONode oNode4 = ONode.ofJson("[{user:null}]");
        assert oNode4.select("$[0].user.name.first").isNull();
        assert oNode4.select("$[0].user.name.first").isUndefined();
        assert oNode4.exists("$[0].user.name.first") == false;

        ONode oNode5 = ONode.ofJson("[{user:{}}]");
        assert oNode5.select("$[0].user.name.first").isNull();
        assert oNode5.select("$[0].user.name.first").isUndefined();
        assert oNode5.exists("$[0].user.name.first") == false;
    }

    @Test
    public void test6() {
        String str = "{'note':null,name:'1'}";
        UserModel userModel = ONode.ofJson(str).toBean( UserModel.class);

        ONode load = ONode.ofJson(str);
        ONode load1 = ONode.ofBean(userModel, Feature.Write_Nulls);


        System.out.println(load.toJson());
        System.out.println(load.exists("$.note"));
        assert load.exists("$.note");


        System.out.println(load1.toJson());
        System.out.println(load1.exists("$.note"));
        assert load1.exists("$.note");

        assert load1.select("$.note").isNull(); //因为
        assert load1.select("$.note").isValue() == false;
        load1.select("$.note").set("a1", 1);
        System.out.println(load1.toJson());
        assert load1.select("$.note").isObject();
    }

    @Test
    public void test7() {
        UserModel userModel1 = new UserModel();
        ONode user1 = ONode.ofBean(userModel1);
        System.out.println(user1.exists("$.note"));
        assert user1.exists("$.note") == false;


        userModel1 = new UserModel();
        user1 = ONode.ofBean(userModel1, Feature.Write_Nulls);
        System.out.println(user1.exists("$.note"));
        assert user1.exists("$.note") == true;
    }
}
