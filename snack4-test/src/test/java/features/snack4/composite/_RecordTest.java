package features.snack4.composite;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 * for jdk14+ record test
 *
 * @author noear 2022/1/12 created
 */
public class _RecordTest {
    @Test
    public void test1() {
        MyRecord tmp = new MyRecord("noear", 12);

        String json = ONode.toJson(tmp);
        System.out.println(json);

        MyRecord tmp2 = ONode.fromJson(json, MyRecord.class);

        assert tmp.username().equals(tmp2.username());

        System.out.println(ONode.toJson(tmp2));
    }

    //for jdk14+
//    public record MyRecord(String username,Integer age) {
//    }

    public static final class MyRecord {
        final String username;
        final Integer age;

        public MyRecord(String username, Integer age) {
            this.username = username;
            this.age = age;
        }

        public String username() {
            return username;
        }

        public Integer age() {
            return age;
        }
    }
}
