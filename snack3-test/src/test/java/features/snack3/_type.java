package features.snack3;

import demo.snack3._models.UserModel;
import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class _type {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    @Test
    public void test2() {
        Type type1 = UserModel.class;
        Type type2 = (new ArrayList<UserModel>() {
        }).getClass().getGenericSuperclass();

        return;
    }

    @Test
    public void test3(){
        Map map = new HashMap();
        map.put("name", ANSI_BLACK);

        System.out.println(ONode.stringify(map));
        System.out.println(ONode.stringify(map));
        System.out.println(ONode.serialize(map));
        System.out.println(JSON.toJSON(map));
    }
}
