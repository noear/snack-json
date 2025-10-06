package features.snack4.composite;

import demo.snack4._model3.Message;
import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;

/**
 * @author noear 2023/2/21 created
 */
public class ClassLoaderTest {
    @Test
    public void demo() {
        Message data = new Message();

        Options options = Options.enableOf(Feature.Write_ClassName);
        //指定类加载器
        options.classLoader(data.getClass().getClassLoader());

        //序列化
        String json = ONode.from(data, options).serialize();

        //反序列化
        data = ONode.load(json, options).to();
    }
}
