package features.snack4.composite;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Feature;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.codec.ObjectEncoder;
import org.noear.snack4.codec.util.DateUtil;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.TimeZone;

/**
 * @author noear 2025/6/25 created
 */
public class OptionTest {
    @Test
    public void case1() throws Exception {
        Options options = Options.def();
        options.enableFeature(Feature.Write_UseNumberString);
        options.enableFeature(Feature.Write_UseDateFormat);
        options.enableFeature(Feature.Write_SerializeNulls);
        options.enableFeature(Feature.Write_EnumUsingName);
        options.dateFormatText("yyyy-MM-dd");
        options.timeZone(TimeZone.getTimeZone("GMT+8"));
        options.addDecoder(BigDecimal.class, (opts, attr, node, clazz) -> null);
        options.addEncoder(BigDecimal.class, (opts, attr, value) -> new ONode());

        ONode oNode = ONode.fromJson("{}", options);

        oNode.create("$.num").setValue(10000L);
        oNode.create("$.date").setValue(DateUtil.parse("2025-06-25"));

        String json = oNode.toJson();
        System.out.println(json);

        assert "{\"num\":\"10000\",\"date\":\"2025-06-25\"}".equals(json);
    }
}
