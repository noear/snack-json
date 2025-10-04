package features.snack3;

import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Options;
import org.noear.snack.core.Context;
import org.noear.snack.core.Feature;
import org.noear.snack.from.JsonFromer;
import org.noear.snack.to.JsonToer;

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
        Context c = new Context(Options.def(), "\"xxx\"");
        new JsonFromer().handle(c);
        assert "xxx".equals(((ONode) c.target).getString());

        c = new Context(Options.def(), "'xxx'");
        new JsonFromer().handle(c);
        assert "xxx".equals(((ONode) c.target).getString());

        c = new Context(Options.def(), "true");
        new JsonFromer().handle(c);
        assert ((ONode) c.target).getBoolean();

        c = new Context(Options.def(), "false");
        new JsonFromer().handle(c);
        assert ((ONode) c.target).getBoolean() == false;

        c = new Context(Options.def(), "123");
        new JsonFromer().handle(c);
        assert 123 == ((ONode) c.target).getInt();

        c = new Context(Options.def(), "null");
        new JsonFromer().handle(c);
        assert ((ONode) c.target).isNull();

        c = new Context(Options.def(), "NaN");
        new JsonFromer().handle(c);
        assert ((ONode) c.target).isNull();

        c = new Context(Options.def(), "undefined");
        new JsonFromer().handle(c);
        assert ((ONode) c.target).isNull();

        long times = System.currentTimeMillis();
        c = new Context(Options.def(), "new Date(" + times + ") ");
        new JsonFromer().handle(c);
        assert ((ONode) c.target).getDate().getTime() == times;

    }

    @Test
    public void test21() throws IOException {
        Context c = new Context(Options.def(), "{'a':'b','c':{'d':'e'},'f':{'g':\"h\"},'i':[{'j':'k','l':'m'},'n']}");

        new JsonFromer().handle(c);

        assert "m".equals(((ONode) c.target).get("i").get(0).get("l").getString());
        assert "n".equals(((ONode) c.target).get("i").get(1).getString());

        c.source = c.target;
        new JsonToer().handle(c);

        System.out.println(c.target);
        assert "{\"a\":\"b\",\"c\":{\"d\":\"e\"},\"f\":{\"g\":\"h\"},\"i\":[{\"j\":\"k\",\"l\":\"m\"},\"n\"]}".equals(c.target);
    }

    @Test
    public void test22() throws IOException {
        Context c = new Context(Options.def(), "{a:\"b\"}");

        new JsonFromer().handle(c);

        assert "b".equals(((ONode) c.target).get("a").getString());

        c.source = c.target;
        new JsonToer().handle(c);

        assert "{\"a\":\"b\"}".equals(c.target);
    }

    @Test
    public void test23() throws IOException {
        Context c = new Context(Options.def(), "{a:{b:{c:{d:{e:'f'}}}}}");

        new JsonFromer().handle(c);

        assert "f".equals(((ONode) c.target).get("a").get("b").get("c").get("d").get("e").getString());

        c.source = c.target;
        new JsonToer().handle(c);

        assert "{\"a\":{\"b\":{\"c\":{\"d\":{\"e\":\"f\"}}}}}".equals(c.target);
    }

    @Test
    public void test24() throws IOException {
        String json = "[[[],[]],[[]],[],[{},{},null]]";

        Context c = new Context(Options.def(), json);

        new JsonFromer().handle(c);

        c.source = c.target;
        new JsonToer().handle(c);

        assert json.equals(c.target);
    }

    @Test
    public void test25() throws IOException {
        Context c = new Context(Options.def(), "[{a:'b'},{c:'d'},[{e:'f'}]]");

        new JsonFromer().handle(c);

        assert "f".equals(((ONode) c.target).get(2).get(0).get("e").getString());

        c.source = c.target;
        new JsonToer().handle(c);

        assert "[{\"a\":\"b\"},{\"c\":\"d\"},[{\"e\":\"f\"}]]".equals(c.target);
    }

    @Test
    public void test26() throws IOException {
        Context c = new Context(Options.def(), "[123,123.45,'123.45','2019-01-02T03:04:05',true,false]");

        new JsonFromer().handle(c);

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


        assert 123 == ((ONode) c.target).get(0).getInt();
        assert 123.45 == ((ONode) c.target).get(1).getDouble();
        assert "123.45".equals(((ONode) c.target).get(2).getString());
        assert "2019-01-02T03:04:05".equals(format.format(((ONode) c.target).get(3).getDate()));
        assert ((ONode) c.target).get(4).getBoolean();
        assert !((ONode) c.target).get(5).getBoolean();

        c.source = c.target;
        new JsonToer().handle(c);

        assert "[123,123.45,\"123.45\",\"2019-01-02T03:04:05\",true,false]".equals(c.target);
    }

    /**
     * 测试：换行符之类的 转码
     */
    @Test
    public void test27() throws IOException {

        Context c = new Context(Options.def(), "{\"a\":\"\\t\"}");

        new JsonFromer().handle(c);

        assert "\t".equals(((ONode) c.target).get("a").getString());


        c.source = c.target;
        new JsonToer().handle(c);

        assert "{\"a\":\"\\t\"}".equals(c.target);

    }



    @Test
    public void test40() throws IOException {
        Throwable err = null;

        try {
            Context c = new Context(Options.of(Feature.BrowserCompatible), "{{\"aaa\":\"111\",\"bbb\":\"222\"}");
            new JsonFromer().handle(c);
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
            Context c = new Context(Options.of(Feature.BrowserCompatible), "[\"\"aaa\",\"bbb\",\"ccc\"]");
            new JsonFromer().handle(c);
        } catch (Throwable e) {
            err = e;
            e.printStackTrace();
        }

        assert err != null;
    }
}