package features.snack4.codec;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.snack4.codec.ObjectEncoder;
import org.noear.snack4.codec.util.DateUtil;

import java.util.Date;

/**
 *
 * @author noear 2025/10/14 created
 *
 */
public class AttrTest {
    @Test
    public void case1() {
        Options options = Options.of()
                .addEncoder(Date.class, (ctx, value, target) -> {
                    return target.setValue(value.getTime());
                });

        DemoDo1 dateDo = new DemoDo1();

        String json = ONode.ofBean(dateDo, options).toJson();
        System.out.println(json);
        assert "{\"date\":1760453997855,\"date2\":\"2025-10-14\"}".equals(json);
    }

    @Test
    public void case1_2() {
        Options options = Options.of(Feature.Read_OnlyUseGetter)
                .addEncoder(Date.class, (ctx, value, target) -> {
                    return target.setValue(value.getTime());
                });

        DemoDo1 dateDo = new DemoDo1();

        String json = ONode.ofBean(dateDo, options).toJson();
        System.out.println(json);
        assert "{\"date\":1760453997855,\"date2\":\"2025-10-14\"}".equals(json);
    }

    @Test
    public void case2() {
        Options options = Options.of(Feature.Read_AllowUseGetter)
                .addEncoder(Date.class, (ctx, value, target) -> {
                    return target.setValue(value.getTime());
                });

        DemoDo2 dateDo = new DemoDo2();

        String json = ONode.ofBean(dateDo, options).toJson();
        System.out.println(json);
        assert "{\"date\":1760453997855,\"date2\":\"2025-10-14\"}".equals(json);
    }

    @Test
    public void case2_2() {
        Options options = Options.of()
                .addEncoder(Date.class, (ctx, value, target) -> {
                    return target.setValue(value.getTime());
                });

        DemoDo2 dateDo = new DemoDo2();

        String json = ONode.ofBean(dateDo, options).toJson();
        System.out.println(json);
        assert "{\"date\":1760453997855,\"date2\":1760453997855}".equals(json);
    }

    @Test
    public void case3() {
        Options options = Options.of(Feature.Read_AllowUseGetter)
                .addEncoder(Date.class, (ctx, value, target) -> {
                    return target.setValue(value.getTime());
                });

        DemoDo3 dateDo = new DemoDo3();

        String json = ONode.ofBean(dateDo, options).toJson();
        System.out.println(json);
        assert "{\"date\":1760453997855,\"date2\":\"2025-10-14\"}".equals(json);
    }

    @Test
    public void case3_2() {
        Options options = Options.of()
                .addEncoder(Date.class, (ctx, value, target) -> {
                    return target.setValue(value.getTime());
                });

        DemoDo3 dateDo = new DemoDo3();

        String json = ONode.ofBean(dateDo, options).toJson();
        System.out.println(json);
        assert "{\"date\":1760453997855,\"date2\":1760453997855}".equals(json);

        DemoDo3 demoDo3 = ONode.ofJson(json, Feature.Write_AllowUseSetter).toBean(DemoDo3.class);

        System.out.println(demoDo3.getDate2().getTime());
        assert demoDo3.getDate2().getTime() == 1L;
    }


    @Setter
    @Getter
    public static class DemoDo1 {
        private Date date = new Date(1760453997855L);

        @ONodeAttr(format = "yyyy-MM-dd")
        private Date date2 = new Date(1760453997855L);
    }

    public static class DemoDo2 {
        private Date date = new Date(1760453997855L);
        private Date date2 = new Date(1760453997855L);

        @ONodeAttr(format = "yyyy-MM-dd")
        public Date getDate2() {
            return date2;
        }
    }

    public static class DemoDo3 {
        private Date date = new Date(1760453997855L);
        private Date date2 = new Date(1760453997855L);

        @ONodeAttr(encoder = DateEncoder.class)
        public Date getDate2() {
            return date2;
        }

        @ONodeAttr(decoder = DateDecoder.class)
        public void setDate2(Date date2) {
            this.date2 = date2;
        }
    }

    public static class DateEncoder implements ObjectEncoder<Date> {

        @Override
        public ONode encode(EncodeContext ctx, Date value, ONode target) {
            return target.setValue(DateUtil.format(value, "yyyy-MM-dd"));
        }
    }

    public static class DateDecoder implements ObjectDecoder<Date> {
        @Override
        public Date decode(DecodeContext<Date> ctx, ONode node) {
            return new Date(1L);
        }
    }
}