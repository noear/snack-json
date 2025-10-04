package features.snack3;

import demo.Book;
import demo.enums.BookType;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Context;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.snack.exception.SnackException;
import org.noear.snack.from.ObjectFromer;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


/**
 * 枚举注解单元测试
 *
 * @author hans
 */
public class EnumTest {

    /**
     * 反序列化测试
     */
    @Test
    public void case1() {

        String poc = "{\"name\":\"西游记\",\"dict\":" + BookType.CLASSICS.getCode() + ",}";
        ONode oNode = ONode.loadStr(poc);
        //解析
        Book tmp = oNode.toObject(Book.class);

        System.out.println(tmp);
        assert BookType.CLASSICS == tmp.getDict();
    }

    /**
     * 序列化测试
     */
    @Test
    public void case2() {
        Book book = new Book();
        book.setName("西游记");
        book.setDict(BookType.CLASSICS);
        ObjectFromer objectFromer = new ObjectFromer();
        Context context = new Context(Options.def(), book);
        objectFromer.handle(context);
        assert context.source == book;
    }

    /**
     * 序列化测试2
     */
    @Test
    public void case3() {
        String json = "{name:'demo',dict:'9'}";

        try {
            ONode.deserialize(json, Book.class);
            assert false;
        } catch (SnackException e) {
            assert true;
        }
    }

    @Test
    public void case4() {
        String s1 = "'input'";
        String s2 = "'number'";
        String s3 = "'select'";
        String s4 = "'switcher'";
        ConfigControlType type1 = ONode.deserialize(s1, ConfigControlType.class);
        ConfigControlType type2 = ONode.deserialize(s2, ConfigControlType.class);
        ConfigControlType type3 = ONode.deserialize(s3, ConfigControlType.class);
        ConfigControlType type4 = ONode.deserialize(s4, ConfigControlType.class);
        System.out.println(type1);
        System.out.println(type2);
        System.out.println(type3);
        System.out.println(type4);

        assert type1 == ConfigControlType.input;
        assert type2 == ConfigControlType.number;
        assert type3 == ConfigControlType.select;
        assert type4 == ConfigControlType.switcher;
    }

    @Test
    public void case5() {
        String s1 = "input";
        String s2 = "number";
        String s3 = "select";
        String s4 = "switcher";
        ConfigControlType type1 = ONode.deserialize(s1, ConfigControlType.class);
        ConfigControlType type2 = ONode.deserialize(s2, ConfigControlType.class);
        ConfigControlType type3 = ONode.deserialize(s3, ConfigControlType.class);
        ConfigControlType type4 = ONode.deserialize(s4, ConfigControlType.class);
        System.out.println(type1);
        System.out.println(type2);
        System.out.println(type3);
        System.out.println(type4);

        assert type1 == ConfigControlType.input;
        assert type2 == ConfigControlType.number;
        assert type3 == ConfigControlType.select;
        assert type4 == ConfigControlType.switcher;
    }

    @Test
    public void case6() {
        String s1 = "\"input\"";
        String s2 = "\"number\"";
        String s3 = "\"select\"";
        String s4 = "\"switcher\"";
        String type1 = ONode.stringify(ConfigControlType.input, Feature.EnumUsingName);
        String type2 = ONode.stringify(ConfigControlType.number, Feature.EnumUsingName);
        String type3 = ONode.stringify(ConfigControlType.select, Feature.EnumUsingName);
        String type4 = ONode.stringify(ConfigControlType.switcher, Feature.EnumUsingName);
        System.out.println(type1);
        System.out.println(type2);
        System.out.println(type3);
        System.out.println(type4);

        assert type1.equals(s1);
        assert type2.equals(s2);
        assert type3.equals(s3);
        assert type4.equals(s4);
    }

    @Test
    public void case7() {
        Map<A, Integer> map = new LinkedHashMap<>();
        map.put(A.A,1);
        map.put(A.B,2);
        Rec rec = new Rec();
        rec.i = 1;
        rec.map = map;
        rec.set = Collections.singleton(3);

        String json = ONode.load(rec, Feature.PrettyFormat).toJson();

        System.out.println(json);
        Rec rec2 = ONode.deserialize(json, Rec.class);

        System.out.println(rec2.toString());

        assert "Rec{i=1, map={B=2, A=1}, set=[3]}".equals(rec2.toString()) ||
                "Rec{i=1, map={A=1, B=2}, set=[3]}".equals(rec2.toString());
    }

    public static enum ConfigControlType {
        input,
        number,
        select,
        switcher,
    }

    public static enum A {
        A,
        B;
    }

    public static class Rec {
        int i;
        Map<A, Integer> map;
        Set<Integer> set;

        @Override
        public String toString() {
            return "Rec{" +
                    "i=" + i +
                    ", map=" + map +
                    ", set=" + set +
                    '}';
        }
    }
}
