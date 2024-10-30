package features;

import _model5.TypeC;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.NameValues;

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

        ONode oNode = ONode.loadObj(props);
        String json = oNode.toJson();

        assert oNode.get("debug").getBoolean();

        System.out.println(json);

        Properties props2 = ONode.loadStr(json).toObject(Properties.class);
        String json2 = ONode.load(props2).toJson();

        System.out.println(json2);

        assert json.length() == json2.length();

        Properties props3 = new Properties();
        ONode.loadStr(json).bindTo(props3);
        String json3 = ONode.load(props3).toJson();

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

        ONode oNode = ONode.loadObj(props);
        System.out.println(oNode.toJson());

        assert oNode.isArray() == true;
        assert oNode.count() == 2;
    }

    @Test
    public void test2() {
        Properties props = new Properties();
        props.setProperty("typeA", "_model5.TypeAImpl");
        props.setProperty("typeB", "_model5.TypeBImpl");

        TypeC typeC = ONode.loadObj(props).toObject(TypeC.class);
        assert typeC.typeA != null;
        System.out.println(typeC.typeA);
        assert typeC.typeB != null;
        System.out.println(typeC.typeB);
    }

    @Test
    public void test3() {
        Properties props = new Properties();
        props.setProperty("type[]", "_model5.TypeAImpl");

        ONode tmp = ONode.loadObj(props).get("type");
        System.out.println(tmp.toJson());

        assert tmp.isArray();
        assert tmp.count() == 1;
    }

    @Test
    public void test4() {
        NameValues nameValues = new NameValues();
        nameValues.add("title", "test");
        nameValues.add("debug", "true");
        nameValues.add("user.id", "1");
        nameValues.add("user.name", "noear");
        nameValues.add("server.urls[0]", "http://x.x.x");
        nameValues.add("server.urls[1]", "http://y.y.y");
        nameValues.add("user.orders[0].items[0].name", "手机");
        nameValues.add("type[]", "a");
        nameValues.add("type[]", "b");

        String json = ONode.loadObj(nameValues).toJson();
        System.out.println(json);

        assert "{\"debug\":\"true\",\"server\":{\"urls\":[\"http://x.x.x\",\"http://y.y.y\"]},\"title\":\"test\",\"type\":[\"a\",\"b\"],\"user\":{\"id\":\"1\",\"name\":\"noear\",\"orders\":[{\"items\":[{\"name\":\"手机\"}]}]}}".equals(json);
    }

    @Test
    public void test5() {
        NameValues nameValues = new NameValues();
        nameValues.add("title", "test");
        nameValues.add("debug", "true");
        nameValues.add("user[id]", "1");
        nameValues.add("user[name]", "noear");

        String json = ONode.loadObj(nameValues).toJson();
        System.out.println(json);

        assert "{\"debug\":\"true\",\"title\":\"test\",\"user\":{\"id\":\"1\",\"name\":\"noear\"}}".equals(json);
    }

    @Test
    public void test6() {
        NameValues nameValues = new NameValues();
        nameValues.add("title", "test");
        nameValues.add("debug", "true");
        nameValues.add("user['id']", "1");
        nameValues.add("user[\"name\"]", "noear");

        String json = ONode.loadObj(nameValues).toJson();
        System.out.println(json);

        assert "{\"debug\":\"true\",\"title\":\"test\",\"user\":{\"name\":\"noear\",\"id\":\"1\"}}".equals(json);
    }

    @Test
    public void test7() {
        String json = "{'userName':'a'}";
        UserModel userModel = ONode.deserialize(json, UserModel.class);
        assert userModel.getUserName() == null;


        userModel = ONode.load(json, Feature.UseSetter).toObject(UserModel.class);
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