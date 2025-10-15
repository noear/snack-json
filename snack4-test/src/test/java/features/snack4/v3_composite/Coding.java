package features.snack4.v3_composite;

import demo.snack4._models.OrderModel;
import demo.snack4._models.UserModel;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
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
        opts.addEncoder(OrderModel.class, (ctx, value, target) -> target.set("id", value.order_id));

        String json = ONode.ofBean(orderModel, opts).toJson();
        System.out.println(json);
        assert json.contains("1");

        //添加解码器
        opts.addDecoder(OrderModel.class, (ctx, node) -> {
            OrderModel tmp = new OrderModel();
            tmp.order_id = node.get("id").getInt();
            return tmp;
        });

        OrderModel rst = ONode.ofJson(json).toBean(OrderModel.class);
        System.out.println(rst);
        assert rst.order_id == 0;

        rst = ONode.ofJson(json, opts).toBean(OrderModel.class);
        System.out.println(rst);
        assert rst.order_id == 1;
    }

    public void demo0() {
        String json = "";

        Options opts = Options.of();
        opts.addDecoder(LocalDateTime.class, (ctx, node) -> {
            //我随手写的，具体要自己解析下格式
            return LocalDateTime.parse(node.getString());
        });

        OrderModel tmp = ONode.ofJson(json, opts).toBean(OrderModel.class);
    }

    @Test
    public void test1() {
        OrderModel orderModel = new OrderModel();
        orderModel.order_id = 1;

        Options options = Options.of();
        options.addEncoder(Date.class, (ctx, value, target) ->target.setValue((DateUtil.format(value, "yyyy-MM-dd"))));

        //添加编码器
        options.addEncoder(OrderModel.class, (ctx, value, target) -> target.set("user", new ONode().set("uid", "1001")).set("order_time", null));


        String json = ONode.ofBean(orderModel, options).toJson();
        System.out.println(json);
        assert json.contains("1001");

        //添加解码器
        options.addDecoder(Date.class, (ctx, node) -> {
            if (node.isNull()) {
                return new Date();
            } else {
                return node.getDate();
            }
        });


        //添加解码器
        options.addDecoder(LocalTime.class, (ctx, node) -> {
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
        options.addDecoder(UserModel.class, (ctx, node) -> {
            UserModel tmp = new UserModel();
            tmp.id = node.get("uid").getInt();
            return tmp;
        });

        OrderModel rst = ONode.ofJson(json).toBean(OrderModel.class);
        System.out.println(rst);
        assert rst.user.id == 0;

        rst = ONode.ofJson(json, options).toBean(OrderModel.class);
        System.out.println(rst);
        assert rst.user.id == 1001;
    }
}