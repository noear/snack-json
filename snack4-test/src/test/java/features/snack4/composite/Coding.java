package features.snack4.composite;

import demo.snack4._models.OrderModel;
import demo.snack4._models.UserModel;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.codec.util.DateUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * @author noear 2021/10/11 created
 */
public class Coding {
    @Test
    public void test0() {
        OrderModel orderModel = new OrderModel();
        orderModel.order_id = 1;
        Options opts = Options.of();
        //添加编码器
        opts.addEncoder(OrderModel.class, (opts1, attr, value) -> new ONode().set("id", value.order_id));

        String json = ONode.from(orderModel, opts).serialize();
        System.out.println(json);
        assert json.contains("1");

        //添加解码器
        opts.addDecoder(OrderModel.class, (opts2, attr, node, clazz) -> {
            OrderModel tmp = new OrderModel();
            tmp.order_id = node.get("id").getInt();
            return tmp;
        });

        OrderModel rst = ONode.load(json).to(OrderModel.class);
        System.out.println(rst);
        assert rst.order_id == 0;

        rst = ONode.load(json, opts).to(OrderModel.class);
        System.out.println(rst);
        assert rst.order_id == 1;
    }

    public void demo0() {
        String json = "";

        Options opts = Options.of();
        opts.addDecoder(LocalDateTime.class, (opts1, attr, node, clazz) -> {
            //我随手写的，具体要自己解析下格式
            return LocalDateTime.parse(node.getString());
        });

        OrderModel tmp = ONode.load(json, opts).to(OrderModel.class);
    }

    @Test
    public void test1() {
        OrderModel orderModel = new OrderModel();
        orderModel.order_id = 1;

        Options options = Options.of();
        options.addEncoder(Date.class, (opts, attr, value) -> new ONode((DateUtil.format(value, "yyyy-MM-dd"))));

        //添加编码器
        options.addEncoder(OrderModel.class, (opts, attr, value) -> new ONode().set("user", new ONode().set("uid", "1001")).set("order_time", null));


        String json = ONode.from(orderModel, options).serialize();
        System.out.println(json);
        assert json.contains("1001");

        //添加解码器
        options.addDecoder(Date.class, (opts, attr, node, clazz) -> {
            if (node.isNull()) {
                return new Date();
            } else {
                return node.getDate();
            }
        });


        //添加解码器
        options.addDecoder(LocalTime.class, (opts, attr, node, clazz) -> {
            if (node.isNull()) {
                return LocalTime.now();
            } else {
                return node.getDate()
                        .toInstant()
                        .atZone(Options.DEF_TIME_ZONE.toZoneId())
                        .toLocalTime();
            }
        });

        //添加解码器
        options.addDecoder(UserModel.class, new ObjectDecoder<UserModel>() {
            @Override
            public UserModel decode(Options opts, ONodeAttr attr, ONode node, Class<?> clazz) {
                UserModel tmp = new UserModel();
                tmp.id = node.get("uid").getInt();
                return tmp;
            }
        });

        OrderModel rst = ONode.load(json).to(OrderModel.class);
        System.out.println(rst);
        assert rst.user.id == 0;

        rst = ONode.load(json, options).to(OrderModel.class);
        System.out.println(rst);
        assert rst.user.id == 1001;
    }
}