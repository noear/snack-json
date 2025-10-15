package features.snack4.v3_composite;

import demo.snack4.Book;
import demo.snack4.enums.BookType;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.core.Feature;
import org.noear.snack4.SnackException;

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

        String poc = "{\"name\":\"西游记\",\"dict\":" + BookType.CLASSICS.getCode() + "}";
        ONode oNode = ONode.ofJson(poc);
        //解析
        Book tmp = oNode.toBean(Book.class);

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

        ONode.ofBean(book); //不出异常即可
    }

    /**
     * 序列化测试2
     */
    @Test
    public void case3() {
        String json = "{name:'demo',dict:'9'}";

        try {
            ONode.ofJson(json).toBean(Book.class);
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
        ConfigControlType type1 = ONode.ofJson(s1).toBean( ConfigControlType.class);
        ConfigControlType type2 = ONode.ofJson(s2).toBean( ConfigControlType.class);
        ConfigControlType type3 = ONode.ofJson(s3).toBean( ConfigControlType.class);
        ConfigControlType type4 = ONode.ofJson(s4).toBean( ConfigControlType.class);
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
        ConfigControlType type1 = ONode.ofBean(s1).toBean(ConfigControlType.class);
        ConfigControlType type2 = ONode.ofBean(s2).toBean( ConfigControlType.class);
        ConfigControlType type3 = ONode.ofBean(s3).toBean( ConfigControlType.class);
        ConfigControlType type4 = ONode.ofBean(s4).toBean( ConfigControlType.class);
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

        String type1 = ONode.ofBean(ConfigControlType.input, Feature.Write_EnumUsingName).toJson();
        String type2 = ONode.ofBean(ConfigControlType.number, Feature.Write_EnumUsingName).toJson();
        String type3 = ONode.ofBean(ConfigControlType.select, Feature.Write_EnumUsingName).toJson();
        String type4 = ONode.ofBean(ConfigControlType.switcher, Feature.Write_EnumUsingName).toJson();

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

        String json = ONode.ofBean(rec, Feature.Write_PrettyFormat).toJson();

        System.out.println(json);
        Rec rec2 = ONode.ofJson(json).toBean( Rec.class);

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
