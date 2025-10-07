package features.snack4.composite;

import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 2019.01.28
 *
 * @author cjl
 */
public class JsonTest {

    /**
     * 测试非对象，非数组数据
     */
    @Test
    public void test11() throws IOException {
       ONode c =  ONode.load("\"xxx\"");
        assert "xxx".equals(c.getString());

        c = ONode.load("'xxx'");
        assert "xxx".equals(c.getString());

        c = ONode.load( "true");
        assert c.getBoolean();

        c = ONode.load("false");
        assert c.getBoolean() == false;

        c = ONode.load("123");
        assert 123 == c.getInt();

        c = ONode.load("null");
        assert c.isNull();

        c = ONode.load("NaN");
        assert c.isNull();

        c = ONode.load( "undefined");
        assert c.isNull();

//        long times = System.currentTimeMillis();
//        c = ONode.load("new Date(" + times + ") ");
//        assert c.getDate().getTime() == times;

    }

    @Test
    public void test21() throws IOException {
        ONode c = ONode.load("{'a':'b','c':{'d':'e'},'f':{'g':\"h\"},'i':[{'j':'k','l':'m'},'n']}");

        assert "m".equals(c.get("i").get(0).get("l").getString());
        assert "n".equals(c.get("i").get(1).getString());

        assert "{\"a\":\"b\",\"c\":{\"d\":\"e\"},\"f\":{\"g\":\"h\"},\"i\":[{\"j\":\"k\",\"l\":\"m\"},\"n\"]}".equals(c.toJson());
    }

    @Test
    public void test22() throws IOException {
        ONode c = ONode.load("{a:\"b\"}");

        assert "b".equals(c.get("a").getString());

        assert "{\"a\":\"b\"}".equals(c.toJson());
    }

    @Test
    public void test23() throws IOException {
       ONode c = ONode.load("{a:{b:{c:{d:{e:'f'}}}}}");

        assert "f".equals(c.get("a").get("b").get("c").get("d").get("e").getString());

        assert "{\"a\":{\"b\":{\"c\":{\"d\":{\"e\":\"f\"}}}}}".equals(c.toJson());
    }

    @Test
    public void test24() throws IOException {
        String json = "[[[],[]],[[]],[],[{},{},null]]";

        ONode c = ONode.load(json);

        assert json.equals(c.toJson());
    }

    @Test
    public void test25() throws IOException {
        ONode c= ONode.load( "[{a:'b'},{c:'d'},[{e:'f'}]]");

        assert "f".equals(c.get(2).get(0).get("e").getString());

        assert "[{\"a\":\"b\"},{\"c\":\"d\"},[{\"e\":\"f\"}]]".equals(c.toJson());
    }

    @Test
    public void test26() throws IOException {
        ONode c = ONode.load("[123,123.45,'123.45','2019-01-02T03:04:05',true,false]");

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        assert 123 == c.get(0).getInt();
        assert 123.45 == c.get(1).getDouble();
        assert "123.45".equals(c.get(2).getString());
        assert "2019-01-02T03:04:05".equals(format.format(c.get(3).getDate()));
        assert c.get(4).getBoolean();
        assert !c.get(5).getBoolean();

        assert "[123,123.45,\"123.45\",\"2019-01-02T03:04:05\",true,false]".equals(c.toJson());
    }

    /**
     * 测试：换行符之类的 转码
     */
    @Test
    public void test27() throws IOException {
        ONode c = ONode.load( "{\"a\":\"\\t\"}");

        assert "\t".equals(c.get("a").getString());

        assert "{\"a\":\"\\t\"}".equals(c.toJson());

    }



    @Test
    public void test40() throws IOException {
        Throwable err = null;

        try {
            ONode.load("{{\"aaa\":\"111\",\"bbb\":\"222\"}", Feature.Read_DisableUnquotedKeys);
        } catch (Throwable e) {
            err = e;
            e.printStackTrace();
        }

        assert err != null;
    }

    @Test
    public void test41() throws IOException {
        Throwable err = null;

        try {
            ONode c = ONode.load("[\"\"aaa\",\"bbb\",\"ccc\"]");
        } catch (Throwable e) {
            err = e;
            e.printStackTrace();
        }

        assert err != null;
    }
}