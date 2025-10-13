package features.snack4.codec;

import javafx.scene.chart.Chart;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

/**
 *
 * @author noear 2025/10/13 created
 *
 */
public class BeanTest {

    @Test
    public void typeTest() throws Exception {
        Bean bean = new Bean();

        bean.charset =  Charset.forName("UTF-8");
        bean.longAdder = new LongAdder();
        bean.doubleAdder = new DoubleAdder();
        bean.file = new File("/a.j");
        bean.address = new  InetSocketAddress("127.0.0.1", 8080);
        bean.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        bean.sqlDate = new Date(System.currentTimeMillis());
        bean.timeZone =   TimeZone.getTimeZone("Asia/Shanghai");
        bean.uri = URI.create("http://127.0.0.1:8080/a.j");
        bean.url = bean.uri.toURL();
        bean.uuid = UUID.randomUUID();


        String json = ONode.serialize(bean);
        System.out.println(json);

        Bean bean2 = ONode.deserialize(json, Bean.class);
        System.out.println(bean2);

        String json2 = ONode.serialize(bean2);
        System.out.println(json2);

        assert json.equals(json2);
    }

    static class Bean {
        public Charset charset;
        public DoubleAdder doubleAdder;
        public LongAdder longAdder;
        public File file;
        public InetSocketAddress address;
        public SimpleDateFormat dateFormat;
        public java.sql.Date sqlDate;
        public TimeZone timeZone;
        public URI uri;
        public URL  url;
        public UUID uuid;

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof Bean)) return false;
            Bean bean = (Bean) object;
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