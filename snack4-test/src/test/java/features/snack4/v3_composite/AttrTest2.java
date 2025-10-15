package features.snack4.v3_composite;

import demo.snack4._models.AttrModel;
import demo.snack4._models.UserModel3;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;

/**
 * @author noear 2021/12/10 created
 */
public class AttrTest2 {
    @Test
    public void test() {
        UserModel3 user = new UserModel3();

        user.id = 1;
        user.name = "noear";
        user.note = "test";

        String json = ONode.ofBean(user).toJson();

        System.out.println(json);

        assert json.contains("noear") == false;
        assert json.contains("test") == false;
    }

    @Test
    public void test1() {
        UserModel3 user = new UserModel3();

        user.id = 1;
        user.name = "noear";
        user.note = "test";

        //全局开启可序列化null
//        Options.features_def = Feature.of(
//                Feature.OrderedField,
//                Feature.WriteDateUseTicks,
//                Feature.TransferCompatible,
//                Feature.StringNullAsEmpty,
//                Feature.QuoteFieldNames,
//                Feature.SerializeNulls);

        //配置 Options 实例，添加可序列化null
        Options options = Options.of();

        String json = ONode.ofBean(user, options).toJson();

        System.out.println(json);

        assert json.contains("noear") == false;
        assert json.contains("test") == false;
        assert json.contains("nullVal") == false;
    }

    @Test
    public void test2() {
        String json = "{id:1, name:'noear', note:'test'}";


        UserModel3 user = ONode.ofJson(json).toBean(UserModel3.class);

        System.out.println(json);

        assert user.id == 1;
        assert user.name == null;
        assert user.note == null;
    }

    @Test
    public void test3_asString() {
        AttrModel attrModel = new AttrModel();
        attrModel.id=1;
        attrModel.traceId=2;
        attrModel.name = "noear";

        String json = ONode.ofBean(attrModel).toJson();

        System.out.println(json);

        assert "{\"id\":1,\"traceId\":\"2\",\"name\":\"noear\"}".equals(json);
    }
}
