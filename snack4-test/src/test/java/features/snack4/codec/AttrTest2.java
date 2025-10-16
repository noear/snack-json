package features.snack4.codec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.annotation.ONodeAttr;

import java.util.Date;

/**
 *
 * @author noear 2025/10/16 created
 *
 */
public class AttrTest2 {
    public static Date now = new Date(1760453997855L);

    @Test
    public void case1() {
        DemoDo1 demoDo1 = new DemoDo1();
        demoDo1.date = now;
        demoDo1.date2 = now;

        String json = ONode.serialize(demoDo1);
        System.out.println(json);

        Assertions.assertEquals("{\"date\":1760453997855}", json);

        json = "{\"date\":1760453997855,\"date2\":1760453997855}";
        DemoDo1 demoDo2 = ONode.deserialize(json, DemoDo1.class);
        Assertions.assertNull(demoDo2.date2);
    }


    public static class DemoDo1 {
        public Date date;

        @ONodeAttr(ignore = true)
        public Date date2;
    }
}