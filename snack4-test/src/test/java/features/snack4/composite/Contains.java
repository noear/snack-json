package features.snack4.composite;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import java.util.Date;
import java.util.List;

public class Contains {
    @Test
    public void test0() {

        assert System.identityHashCode(null) == 0;

        assert System.identityHashCode(0) != Integer.hashCode(0);

        assert System.identityHashCode(0l) != Long.hashCode(0);

        assert System.identityHashCode(0) != System.identityHashCode(0l);
    }

    @Test
    public void test1() {
        JSONArray tmp = (JSONArray) JSONArray.parse("[1,2,3,4,5]");
        assert tmp.contains(2);

        tmp = (JSONArray) JSONArray.parse("[1,'2',3,4,5]");
        assert tmp.contains("2");

        long times = System.currentTimeMillis();
        Date time = new Date(times);

        tmp = (JSONArray) JSONArray.parse("[1,'2',3,4,new Date(" + times + ")]");
        assert tmp.contains(time);
    }

    @Test
    public void test12() {
        JSONArray tmp = (JSONArray) JSONArray.parse("[1,2,{c:1,d:2,b:[4]}]");

        JSONObject tmp2 = (JSONObject) JSONObject.parse("{c:1,d:2,b:[4]}");

        assert tmp.contains(tmp2);
    }

    @Test
    public void test21() {

        assert Long.hashCode(2) == new ONode().setValue(2).hashCode();

        ONode tmp = ONode.load("[1,2,3,4,5]");
        assert tmp.getArray().contains(new ONode().setValue(2));

        tmp = ONode.load("[1,'2',3,4,5]");
        assert tmp.getArray().contains(new ONode().setValue("2"));

        long times = System.currentTimeMillis();
        Date time = new Date(times);

        tmp = ONode.load("[1,'2',3,4,new Date(" + times + ")]");
        assert tmp.getArray().contains(new ONode().setValue(time));
    }

    @Test
    public void test22() {

        assert Long.hashCode(2) == new ONode().setValue(2).hashCode();

        ONode tmp = ONode.load("[1,2,3,4,5,true, null]");
        assert tmp.getArray().contains(2l);

        assert tmp.getArray().contains(2);

        assert tmp.getArray().contains(true);

        assert tmp.getArray().contains(null);

        assert tmp.getArray().contains(new ONode());

        tmp = ONode.load("[1,'2',3,4,5]");
        assert tmp.getArray().contains("2");

        long times = System.currentTimeMillis();
        Date time = new Date(times);

        tmp = ONode.load("[1,'2',3,4,new Date(" + times + ")]");
        assert tmp.getArray().contains(time);
    }

    @Test
    public void test3() {

        ONode tmp = ONode.load("{a:[1,2,3,4,5],b:2}");
        ONode tmp2 = ONode.load("{a:[1,2,3,4,5],b:2}");

        assert tmp.equals(tmp2);

        ONode tmp3 = ONode.load("[1,2,3,4,5]");
        List<Integer> tmp4 = ONode.load("[1,2,3,4,5]").to(List.class);

        List<Integer> tmp41 = ONode.load("[1,2,3,5,4]").to(List.class);
        List<Integer> tmp42 = ONode.load("[1,2,3,4]").to(List.class);

        assert  tmp.getObject().containsKey("a");
        assert  tmp.getObject().containsValue(tmp3);
        assert  tmp.getObject().containsValue(tmp4);

        assert  tmp.getObject().containsValue(tmp41) == false;
        assert  tmp.getObject().containsValue(tmp42) == false;

        assert  tmp.getObject().containsValue(2);
    }

    @Test
    public void test4() {
        ONode tmp = ONode.load("[1,2,{c:1,d:2,b:[4]}]");

        ONode tmp2 = ONode.load("{c:1,d:2,b:[4]}");

        assert tmp.getArray().contains(tmp2);

        assert tmp.getArray().indexOf(tmp2) == 2;
    }
}
