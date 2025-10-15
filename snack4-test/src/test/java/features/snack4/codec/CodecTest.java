package features.snack4.codec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.codec.CodecException;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

/**
 *
 * @author noear 2025/10/13 created
 *
 */
public class CodecTest {

    @Test
    public void case1() throws Exception {
        DemoBean bean = new DemoBean();

        bean.charset = Charset.forName("UTF-8");
        bean.longAdder = new LongAdder();
        bean.doubleAdder = new DoubleAdder();
        bean.file = new File("/a.j");
        bean.address = new InetSocketAddress("127.0.0.1", 8080);
        bean.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        bean.sqlDate = new Date(System.currentTimeMillis());
        bean.sqlTime = new Time(System.currentTimeMillis());
        bean.timeZone = TimeZone.getTimeZone("Asia/Shanghai");
        bean.uri = URI.create("http://127.0.0.1:8080/a.j");
        bean.url = bean.uri.toURL();
        bean.uuid = UUID.randomUUID();
        bean.currency = Currency.getInstance("USD");
        bean.duration = Duration.ofSeconds(30);
        bean.collection = Arrays.asList("a", "b", "c");


        String json = ONode.serialize(bean);
        System.out.println(json);

        DemoBean bean2 = ONode.ofJson(json).toBean(DemoBean.class);
        System.out.println(bean2);

        String json2 = ONode.serialize(bean2);
        System.out.println(json2);

        assert json.equals(json2);
    }

    @Test
    public void case2() {
        Assertions.assertThrows(CodecException.class, () -> {
            ONode.ofJson("{'file':111}").toBean(DemoBean.class);
        });

        Assertions.assertThrows(CodecException.class, () -> {
            ONode.ofJson("{'dateFormat':111}").toBean(DemoBean.class);
        });

        Assertions.assertThrows(CodecException.class, () -> {
            ONode.ofJson("{'url':111}").toBean(DemoBean.class);
        });
    }

    static class DemoBean {
        public Charset charset;
        public DoubleAdder doubleAdder;
        public LongAdder longAdder;
        public File file;
        public InetSocketAddress address;
        public SimpleDateFormat dateFormat;
        public java.sql.Date sqlDate;
        public java.sql.Time sqlTime;
        public TimeZone timeZone;
        public URI uri;
        public URL url;
        public UUID uuid;
        public Currency currency;
        public Duration duration;
        public Collection<String> collection;

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof DemoBean)) return false;
            DemoBean bean = (DemoBean) object;
            return Objects.equals(charset, bean.charset) && Objects.equals(doubleAdder, bean.doubleAdder) && Objects.equals(longAdder, bean.longAdder) && Objects.equals(file, bean.file) && Objects.equals(address, bean.address) && Objects.equals(dateFormat, bean.dateFormat) && Objects.equals(sqlDate, bean.sqlDate) && Objects.equals(timeZone, bean.timeZone) && Objects.equals(uri, bean.uri) && Objects.equals(url, bean.url) && Objects.equals(uuid, bean.uuid);
        }

        @Override
        public int hashCode() {
            return Objects.hash(charset, doubleAdder, longAdder, file, address, dateFormat, sqlDate, timeZone, uri, url, uuid);
        }

        @Override
        public String toString() {
            return "Bean{" +
                    "charset=" + charset +
                    ", doubleAdder=" + doubleAdder +
                    ", longAdder=" + longAdder +
                    ", file=" + file +
                    ", address=" + address +
                    ", dateFormat=" + dateFormat +
                    ", sqlDate=" + sqlDate +
                    ", timeZone=" + timeZone +
                    ", uri=" + uri +
                    ", url=" + url +
                    ", uuid=" + uuid +
                    '}';
        }
    }
}