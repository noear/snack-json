package features.snack4.v3_composite;

import org.junit.jupiter.api.Test;
import org.noear.snack4.node.Feature;
import org.noear.snack4.ONode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author noear 2021/9/11 created
 */
public class TemporaryClassTest {
    @Test
    public void test() {
        List<Map> list = new ArrayList();
        list.add(new HashMap<>());
        list.add(new HashMap<>());

        List userServices = new ArrayList();

        list.forEach(tmp -> {
            tmp.put("service", new HashMap<String, Object>() {{ //这是个临时类
                put("name", "noear");
                put("icon", "");
            }});

            userServices.add(tmp);
        });

        String json = ONode.ofBean(userServices, Feature.Write_ClassName).toJson();
        System.out.println(json);

        //临时类会自动转换为父类，并处理成功
        ONode.ofJson(json).toBean();
    }


    @Test
    public void test2(){
        List<Map> list = new ArrayList();
        list.add(new HashMap<>());
        list.add(new HashMap<>());

        List userServices = new ArrayList();

        list.forEach(tmp->{
            Map<String,Object> service = new HashMap<>();
            service.put("name","noear");
            service.put("icon","");

            tmp.put("service", service);

            userServices.add(tmp);
        });

        String json = ONode.ofBean(userServices, Feature.Write_ClassName).toJson();
        System.out.println(json);

        Object obj = ONode.ofJson(json);
        assert obj != null;
    }
}
