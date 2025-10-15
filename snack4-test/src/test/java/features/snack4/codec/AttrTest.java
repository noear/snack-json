package features.snack4.codec;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.util.DateUtil;
import org.noear.snack4.util.Asserts;

import java.util.Date;

/**
 *
 * @author noear 2025/10/14 created
 *
 */
public class AttrTest {
    @Test
    public void case1() {
        Options options = Options.of().addEncoder(Date.class, (ctx, value, target) -> {
//            if (ctx.getAttr() != null) {
//                if (Asserts.isNotEmpty(ctx.getAttr().getFormat())) {
//                    return target.setValue(ctx.getAttr().formatDate(value));
//                }
//            }

            return target.setValue(value.getTime());
        });

        CustomDateDo dateDo = new CustomDateDo();

        String json = ONode.ofBean(dateDo, options).toJson();
        System.out.println(json);
        assert "{\"date\":1760453997855,\"date2\":\"2025-10-14\"}".equals(json);
    }


    @Setter
    @Getter
    public static class CustomDateDo {
        private Date date = new Date(1760453997855L);

        @ONodeAttr(format = "yyyy-MM-dd")
        private Date date2 = new Date(1760453997855L);
    }
}