package features.snack4.v3_composite;

import demo.snack4._models.TimeModel;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author noear 2024/5/9 created
 */
public class TimeTest {
    @Test
    public void test1() {
        TimeModel timeModel = new TimeModel();
        timeModel.time = LocalDateTime.now();
        timeModel.date = LocalDate.now();
        timeModel.date2 = new Date();

        String json = ONode.serialize(timeModel);
        System.out.println(json);
    }
}
