package features.snack4.composite;

import demo.snack4._models.OffsetTimeModel;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import java.time.OffsetTime;
import java.time.ZoneOffset;

/**
 * OffsetTime序列化测试
 * @author hans
 */
public class OffsetTimeTest {


    /**
     * 反序列化测试(不带时区偏移)
     */
    @Test
    public void deserialize() {
        String poc = "{\"time\":\"20:54:51\"}";
        ONode oNode = ONode.ofJson(poc);
//        //解析
        OffsetTimeModel model = oNode.toBean(OffsetTimeModel.class);
        OffsetTime time0 = model.time;
        // 转到0时区
        OffsetTime time1 = time0.withOffsetSameInstant(ZoneOffset.of("Z"));
        assert time0.isEqual(time1);
    }

    /**
     * 反序列化测试(带时区偏移)
     */
    @Test
    public void deserializeOffset() {
        String poc = "{\"time\":\"20:54:51+08:00\"}";
        ONode oNode = ONode.ofJson(poc);
//        //解析
        OffsetTimeModel model = oNode.toBean(OffsetTimeModel.class);
        OffsetTime time0 = model.time;
        // 转到0时区
        OffsetTime time1 = time0.withOffsetSameInstant(ZoneOffset.of("Z"));
        assert time0.isEqual(time1);
    }


    /**
     * 序列化测试
     */
    @Test
    public void serialize() {
        OffsetTimeModel data = new OffsetTimeModel();
        data.time = OffsetTime.of(2, 3, 1, 0, ZoneOffset.of("+03:00"));

        ONode.ofBean(data); //不出异常就行
    }
}