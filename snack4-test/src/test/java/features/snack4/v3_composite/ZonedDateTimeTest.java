package features.snack4.v3_composite;

import demo.snack4._models.ZonedDateTimeModel;
import org.junit.jupiter.api.Test;
import org.noear.snack4.node.Feature;
import org.noear.snack4.ONode;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * ZoneDateTime序列化测试
 * @author hans
 */
public class ZonedDateTimeTest {


    /**
     * 反序列化测试
     */
    @Test
    public void deserialize() {
        String poc = "{\"date\":\"2024-01-12T10:30:00.000+03:00\"}";
        ONode oNode = ONode.ofJson(poc);
        //解析
        ZonedDateTimeModel model = oNode.toBean(ZonedDateTimeModel.class);
        ZonedDateTime date = model.date;
        ZonedDateTime zonedDateTime = date.withZoneSameInstant(ZoneId.of("GMT+3"));
        assert date.toInstant().equals(zonedDateTime.toInstant());
    }

    /**
     * 序列化测试
     */
    @Test
    public void serialize() {
        ZonedDateTimeModel data = new ZonedDateTimeModel();
        data.date = ZonedDateTime.now();

        ONode.ofBean(data, Feature.Write_ClassName).toJson(); //无异常就好
    }
}