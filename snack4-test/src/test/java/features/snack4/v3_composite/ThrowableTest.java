package features.snack4.v3_composite;

import org.junit.jupiter.api.Test;
import org.noear.snack4.core.Feature;
import org.noear.snack4.ONode;

public class ThrowableTest {
    public Object test01_exc(){
        try {
            return "".substring(2, 10);
        }catch (Throwable ex){
            return ex;
        }
    }

    @Test
    public void test01() throws Throwable{
        Throwable err = (Throwable)test01_exc();
        err.printStackTrace();

        String tmp_json = ONode.ofBean(err, Feature.Write_ClassName).toJson();
        System.out.println(tmp_json);

        Throwable err2 = ONode.ofJson(tmp_json).toBean( Throwable.class);
        err2.printStackTrace();
    }

    @Test
    public void test02() throws Throwable{
        Throwable tmp = (Throwable)test01_exc();
        Throwable err = new RuntimeException(tmp);
        err.printStackTrace();

        String tmp_json = ONode.ofBean(err, Feature.Write_ClassName).toJson();
        System.out.println(tmp_json);

        Throwable err2 = ONode.ofJson(tmp_json).toBean(Throwable.class);
        err2.printStackTrace();
    }

    @Test
    public void test03() throws Throwable{
        Throwable tmp = (Throwable)test01_exc();
        Throwable tmp2 = new Exception(tmp);
        Throwable err = new RuntimeException(tmp2);
        err.printStackTrace();

        String tmp_json = ONode.ofBean(err, Feature.Write_ClassName).toJson();
        System.out.println(tmp_json);

        Throwable err2 = ONode.ofJson(tmp_json).toBean(Throwable.class);
        err2.printStackTrace();
    }
}
