package features.snack4.composite;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Feature;
import org.noear.snack4.Options;
import org.noear.snack4.codec.util.DateUtil;

import java.math.BigDecimal;
import java.util.TimeZone;

/**
 * @author noear 2025/6/25 created
 */
public class OptionTest {
    @Test
    public void case1() throws Exception {
        Options options = Options.of();
        options.addFeature(Feature.Write_NumbersAsString);
        options.addFeature(Feature.Write_UseDateFormat);
        options.addFeature(Feature.Write_Nulls);
        options.addFeature(Feature.Write_EnumUsingName);
        options.dateFormat("yyyy-MM-dd");
        options.timeZone(TimeZone.getTimeZone("GMT+8"));
        options.addDecoder(BigDecimal.class, (ctx, node) -> null);
        options.addEncoder(BigDecimal.class, (ctx, value, target) -> target);

        ONode oNode = ONode.ofJson("{}", options);

        oNode.create("$.num").setValue(10000L);
        oNode.create("$.date").setValue(DateUtil.parse("2025-06-25"));

        String json = oNode.toJson();
        System.out.println(json);

        assert "{\"num\":\"10000\",\"date\":\"2025-06-25\"}".equals(json);
    }
}
