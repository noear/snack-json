package features.snack4.v3_composite;

import demo.snack4._model5.TypeC;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Feature;

import java.util.Properties;

/**
 * @author noear 2022/2/20 created
 */
public class PropertiesTest {
    @Test
    public void test() {
        Properties props = new Properties();
        props.setProperty("title", "test");
        props.setProperty("debug", "true");
        props.setProperty("user.id", "1");
        props.setProperty("user.name", "noear");
        props.setProperty("server.urls[0]", "http://x.x.x");
        props.setProperty("server.urls[1]", "http://y.y.y");
        props.setProperty("user.orders[0].items[0].name", "手机");

        ONode oNode = ONode.ofBean(props);
        String json = oNode.toJson();

        assert oNode.get("debug").getBoolean();

        System.out.println(json);

        Properties props2 = ONode.ofJson(json).toBean(Properties.class);
        String json2 = ONode.ofBean(props2).toJson();

        System.out.println(json2);

        assert json.length() == json2.length();

        Properties props3 = new Properties();
        ONode.ofJson(json).bindTo(props3);
        String json3 = ONode.ofBean(props3).toJson();

        System.out.println(json3);

        assert json.length() == json3.length();

    }

    @Test
    public void test1() {
        Properties props = new Properties();
        props.setProperty("[0].id", "1");
        props.setProperty("[0].name", "id1");
        props.setProperty("[1].id", "2");
        props.setProperty("[1].name", "id2");

        ONode oNode = ONode.ofBean(props);
        System.out.println(oNode.toJson());

        assert oNode.isArray() == true;
        assert oNode.size() == 2;
    }

    @Test
    public void test2() {
        Properties props = new Properties();
        props.setProperty("typeA", "demo.snack4._model5.TypeAImpl");
        props.setProperty("typeB", "demo.snack4._model5.TypeBImpl");

        TypeC typeC = ONode.ofBean(props).toBean(TypeC.class);
        assert typeC.typeA != null;
        System.out.println(typeC.typeA);
        assert typeC.typeB != null;
        System.out.println(typeC.typeB);
    }

    @Test
    public void test3() {
        Properties props = new Properties();
        props.setProperty("type[]", "_model5.TypeAImpl");

        ONode tmp = ONode.ofBean(props).get("type");
        System.out.println(tmp.toJson());

        assert tmp.isArray();
        assert tmp.size() == 1;
    }

    @Test
    public void test4() {
        Properties nameValues = new Properties();
        nameValues.put("title", "test");
        nameValues.put("debug", "true");
        nameValues.put("user.id", "1");
        nameValues.put("user.name", "noear");
        nameValues.put("server.urls[0]", "http://x.x.x");
        nameValues.put("server.urls[1]", "http://y.y.y");
        nameValues.put("user.orders[0].items[0].name", "手机");
        nameValues.put("type[0]", "a");
        nameValues.put("type[1]", "b");

        String json = ONode.ofBean(nameValues).toJson();
        System.out.println(json);

        assert "{\"debug\":\"true\",\"server\":{\"urls\":[\"http://x.x.x\",\"http://y.y.y\"]},\"title\":\"test\",\"type\":[\"a\",\"b\"],\"user\":{\"id\":\"1\",\"name\":\"noear\",\"orders\":[{\"items\":[{\"name\":\"手机\"}]}]}}".equals(json);
    }

    @Test
    public void test5() {
        Properties nameValues = new Properties();
        nameValues.put("title", "test");
        nameValues.put("debug", "true");
        nameValues.put("user[id]", "1");
        nameValues.put("user[name]", "noear");

        String json = ONode.ofBean(nameValues).toJson();
        System.out.println(json);

        assert "{\"debug\":\"true\",\"title\":\"test\",\"user\":{\"id\":\"1\",\"name\":\"noear\"}}".equals(json);
    }

    @Test
    public void test6() {
        Properties nameValues = new Properties();
        nameValues.put("title", "test");
        nameValues.put("debug", "true");
        nameValues.put("user['id']", "1");
        nameValues.put("user[\"name\"]", "noear");

        String json = ONode.ofBean(nameValues).toJson();
        System.out.println(json);

        assert "{\"debug\":\"true\",\"title\":\"test\",\"user\":{\"name\":\"noear\",\"id\":\"1\"}}".equals(json);
    }

    @Test
    public void test7() {
        String json = "{'userName':'a'}";
        UserModel userModel = ONode.deserialize(json, UserModel.class);
        assert userModel.getUserName() == null;


        userModel = ONode.ofJson(json, Feature.Write_UseOnlySetter).toBean(UserModel.class);
        assert "a".equals(userModel.getUserName());
    }

    public static class UserModel {
        private String name;

        public String getUserName() {
            return name;
        }

        public void setUserName(String name) {
            this.name = name;
        }
    }
}