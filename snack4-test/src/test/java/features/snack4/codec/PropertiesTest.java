package features.snack4.codec;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.codec.KeyValueList;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author noear 2025/10/15 created
 *
 */
public class PropertiesTest {
    @Test
    public void case1() {
        Properties properties = new Properties();
        properties.put("aaa[1]", "2");
        properties.put("aaa[0]", "1");
        properties.put("id", "3");

        UserModel rst = ONode.ofBean(properties, Feature.Write_AllowUseSetter).toBean(UserModel.class);
        String json = ONode.serialize(rst);
        System.out.println(json);

        assert json.equals("{\"id\":3,\"aaa\":[1,2]}");
    }

    @Test
    public void case1_2() {
        Properties properties = new Properties();
        properties.put("aaa[1]", "2");
        properties.put("aaa[0]", "1");
        properties.put("id", "3");

        UserModel rst = ONode.ofBean(properties, Feature.Write_AllowUseSetter).toBean(UserModel.class);
        String json = ONode.ofBean(rst, Feature.Node_ToStringUseJson).toString();
        System.out.println(json);

        assert json.equals("{\"id\":3,\"aaa\":[1,2]}");
    }

    @Test
    public void case2() {
        KeyValueList properties = new KeyValueList();
        properties.add("aaa[]", "1");
        properties.add("aaa[]", "2");
        properties.add("id", "3");

        UserModel rst = ONode.ofBean(properties, Feature.Write_AllowUseSetter).toBean(UserModel.class);
        String json = ONode.serialize(rst);
        System.out.println(json);

        assert json.equals("{\"id\":3,\"aaa\":[1,2]}");
    }

    @Data
    public static class UserModel implements Serializable {
        private int id;

        private long[] aaa;
    }
}