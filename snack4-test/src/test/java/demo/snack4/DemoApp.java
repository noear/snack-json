package demo.snack4;

import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.codec.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class DemoApp {
    public static void main(String[] args) {
        Options options = Options.builder()
                .addEncoder(Date.class, (opts, attr, data) -> {
                    return new ONode(DateUtil.format(data, "yyyy-MM-dd"));
                })
                .enable(Feature.Write_PrettyFormat)
                .dateFormat(new SimpleDateFormat("yyyy-MM"))
                .build();

        String json = ONode.load(null, options).toJson();
    }
}
