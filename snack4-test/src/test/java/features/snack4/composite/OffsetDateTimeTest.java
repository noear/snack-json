package features.snack4.composite;

import demo.snack4._models.OffsetDateTimeModel;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * OffsetDateTime序列化测试
 * @author hans
 */
public class OffsetDateTimeTest {
    /**
     * 反序列化测试
     */
    @Test
    public void deserialize() {
        String poc = "{\"date\":\"2024-01-12T10:30:00.000+03:00\"}";
        ONode oNode = ONode.ofJson(poc);
        //解析
        OffsetDateTimeModel model = oNode.toBean(OffsetDateTimeModel.class);
        OffsetDateTime date = model.date;
        OffsetDateTime offsetDateTime = date.withOffsetSameInstant(ZoneOffset.of("+03:00"));
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println(df.format(offsetDateTime));
        System.out.println(df.format(date));
        assert date.toInstant().equals(offsetDateTime.toInstant());
    }

    /**
     * 序列化测试
     */
    @Test
    public void serialize() {
        OffsetDateTimeModel data = new OffsetDateTimeModel();
        data.date = OffsetDateTime.now();

        ONode.ofBean(data); //不异常就行
    }
}