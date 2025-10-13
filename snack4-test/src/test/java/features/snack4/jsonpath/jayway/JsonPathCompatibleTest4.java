package features.snack4.jsonpath.jayway;

import com.jayway.jsonpath.JsonPath;
import features.snack4.v3_composite.JsonPathTest3;
import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author noear 2025/10/7 created
 *
 */
public class JsonPathCompatibleTest4 {

    @Test
    public void case1() {
        final String json = "{\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95},{\"category\":\"fiction\",\"author\":\"Evelyn Waugh\",\"title\":\"Sword of Honour\",\"price\":12.99},{\"category\":\"fiction\",\"author\":\"Herman Melville\",\"title\":\"Moby Dick\",\"isbn\":\"0-553-21311-3\",\"price\":8.99},{\"category\":\"fiction\",\"author\":\"J. R. R. Tolkien\",\"title\":\"The Lord of the Rings\",\"isbn\":\"0-395-19395-8\",\"price\":22.99}],\"bicycle\":{\"color\":\"red\",\"price\":19.95}},\"expensive\":10}";

        compatible_do("1", json, "$..book[2]");
        compatible_do("6", json, "$..book[-2]");
    }

    @Test
    public void case2() {
        final String json = ONode.ofJson("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}").toJson();

        compatible_do("1", json, "$.*.list[0]");
        compatible_do("2", json, "$.*.list[0][0]");
        compatible_do("3", json, "$..list[0][0]");
    }

    @Test
    public void case3() {
        List<JsonPathTest3.Entity> entities = new ArrayList<JsonPathTest3.Entity>();
        entities.add(new JsonPathTest3.Entity(1001, "ljw2083"));
        entities.add(new JsonPathTest3.Entity(1002, "wenshao"));
        entities.add(new JsonPathTest3.Entity(1003, "yakolee"));
        entities.add(new JsonPathTest3.Entity(1004, null));
        String json = ONode.ofBean(entities).toJson();

        compatible_do("1", json, "$[?(@.id in [1001,1002])]");
    }

    @Test
    public void case4() {
        JsonPathTest3.Entity entity = new JsonPathTest3.Entity(1001, "ljw2083");
        String json = ONode.ofBean(entity).toJson();

        compatible_do("1", json, "$[?(@.id == 1001)]");
    }

    @Test
    public void case5() {
        String json = "{\"school\":[{\"name\":\"清华\",\"grade\":[{\"class\":\"二\",\"manSum\":12},{\"class\":\"一班\",\"manSum\":12}]},{\"name\":\"北大\",\"grade\":[{\"class\":\"二\",\"manSum\":12},{\"class\":\"一班\",\"manSum\":12}]}]}";

        compatible_do("1", json, "$.school[?(@.name == '清华')]");
        compatible_do("2", json, "$.school[?(@.name == '清华')].grade[1]");
        compatible_do("3", json, "$.school[?(@.name == '清华')].grade[1][?(@.class == '一班')]");
        compatible_do("4", json, "$.school[?(@.name == '清华')].grade[1][?(@.class == '一班')].manSum");
        //compatible_do("5", json, "$.school[?(@.name == '清华')].grade[1][?(@.class == '一班')].manSum.sum()"); //jayway 会出错
    }

    @Test
    public void case6() {
        //1.加载json
        String json = ONode.ofJson("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5],b:2,ary2:[{a:2,b:8},{a:3,b:{c:'ddd',b:23}}]}}").toJson();

        compatible_do("1", json, "$..b");
        compatible_do("2", json, "$..b.min()");
        compatible_do("3", json, "$..b.max()");
        compatible_do("4", json, "$..b.avg()");
    }

    @Test
    public void case7() {
        //1.加载json
        String json = ONode.ofJson("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5],b:2,ary2:[{a:2,b:8},{a:3,b:{c:'ddd',b:23}}]}}").toJson();

        compatible_do("11", json, "$..list");
        compatible_do("12", json, "$..list[*].min()");
        compatible_do("13", json, "$..list[*].max()");
        compatible_do("14", json, "$..list[*].avg()");
    }

    private void compatible_do(String hint, String json, String jsonpathStr) {
        System.out.println("::::" + hint);
        ONode tmp = null;
        Object tmp2 = null;
        Throwable err1 = null;
        Throwable err2 = null;

        try {
            tmp = ONode.ofJson(json, Options.of().addFeature(Feature.JsonPath_JaywayMode)).select(jsonpathStr);
            System.out.println(tmp.toJson());
        } catch (Throwable e) {
            err1 = e;
            System.err.println(e.getMessage());
        }

        try {
            tmp2 = JsonPath.read(json, jsonpathStr);
            System.out.println(tmp2);
        } catch (Throwable e) {
            err2 = e;
            System.err.println(e.getMessage());
        }

        if (err1 != null && err2 != null) {
            return;
        }

        assert tmp.toJson().equals(tmp2.toString());
    }
}
