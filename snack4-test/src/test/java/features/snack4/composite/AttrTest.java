package features.snack4.composite;

import demo.snack4._models.NodeEncoderImpl;
import demo.snack4._models.UserModel2;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import java.util.Date;

/**
 * @author noear 2021/12/10 created
 */
public class AttrTest {
    @Test
    public void test(){
        UserModel2 user = new UserModel2();

        user.id = 1;
        user.name = "noear";
        user.note  ="test";

        String json= ONode.serialize(user);

        System.out.println(json);

        assert json.contains("noear") == false;
        assert json.contains("test") == true;
    }

    @Test
    public void test2(){
        String json = "{id:1,name:'noear',note:'test',nodeEncoder:{}}";
        UserModel2 user =  ONode.deserialize(json,UserModel2.class );

        System.out.println(user);
        assert "noear".equals(user.name);
        assert null == user.note;
    }

    @Test
    public void test2_1(){
        String json = "{id:1,name:'noear',note:'test',nodeEncoder:'demo.snack4._models.NodeEncoderImpl'}";
        UserModel2 user =  ONode.deserialize(json,UserModel2.class );

        assert user.nodeEncoder != null;
        assert user.nodeEncoder.getClass() == NodeEncoderImpl.class;
        assert "noear".equals(user.name);
        assert null == user.note;
    }

    @Test
    public void test3(){
        UserModel2 user =  new UserModel2();
        user.date = new Date();

        String json = ONode.serialize(user);
        System.out.println(json);

        assert json.contains("date");
        assert json.contains("-") == false;
    }
}
