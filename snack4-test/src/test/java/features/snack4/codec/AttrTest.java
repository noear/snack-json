package features.snack4.codec;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;

import java.util.Date;

/**
 *
 * @author noear 2025/10/14 created
 *
 */
public class AttrTest {
    @Test
    public void case1() {
        Options options = Options.of().addEncoder(Date.class, (ctx, value, target) -> target.setValue(value.getTime()));

        CustomDateDo dateDo = new CustomDateDo();

        String json = ONode.serialize(dateDo, options);
        System.out.println(json);
        assert json.contains("-");
    }


    @Setter
    @Getter
    public class CustomDateDo {
        private Date date = new Date(1760453997855L);

        @ONodeAttr(format = "yyyy-MM-dd")
        private Date date2 = new Date(1760453997855L);
    }
}