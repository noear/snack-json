package features.snack4.path.manual;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

public class Func_min_max_avg_sum_Test {
    //不开启 jayway 特性
    private static final String JSON_DATA = "{" +
            "\"store\": {" +
            "\"book\": [" +
            "{\"category\": \"reference\", \"author\": \"Nigel Rees\", \"title\": \"Sayings of the Century\", \"price\": 8.95, \"ratings\": [4, 5, 4]}," +
            "{\"category\": \"fiction\", \"author\": \"Evelyn Waugh\", \"title\": \"Sword of Honour\", \"price\": 12.99, \"ratings\": [5, 5, 5]}," +
            "{\"category\": \"fiction\", \"author\": \"Herman Melville\", \"title\": \"Moby Dick\", \"isbn\": \"0-553-21311-3\", \"price\": 8.99, \"ratings\": [3, 4, 3, 5]}," +
            "{\"category\": \"fiction\", \"author\": \"J. R. R. Tolkien\", \"title\": \"The Lord of the Rings\", \"isbn\": \"0-395-19395-8\", \"price\": 22.99, \"ratings\": [5, 5, 5, 5, 5]}" +
            "]," +
            "\"bicycle\": {\"color\": \"red\", \"price\": 19.95, \"stock\": 10}," +
            "\"staff\": [" +
            "{\"name\": \"Alice\", \"age\": 30}," +
            "{\"name\": \"Bob\", \"age\": 25}," +
            "{\"name\": \"Charlie\", \"age\": 40}" +
            "]," +
            "\"inventory\": [10, 5, 20, 15]" +
            "}," +
            "\"totalPrice\": 53.92" +
            "}";

    private static DocumentContext context;
    private static ONode oNode;

    @BeforeAll
    static void setup() {
        context = JsonPath.parse(JSON_DATA);
        oNode = ONode.ofJson(JSON_DATA);
    }

    @Test
    public void minTest() {
        compatible_num("1", "8.95", "$.store.book[*].price.min()"); //8.95
        compatible_num("2", "5.0", "$.store.inventory.min()"); //5.0
        compatible_num("3", "8.99", "$.store.book[?(@.category == 'fiction')].price.min()");//8.99
        compatible_num("4", "3.0", "$..ratings[*].min()"); //3.0
        compatible_num("5", "3.0", "$.store.book[2].ratings.min()");
        compatible_num("6", "25.0", "$.store.staff[*].age.min()");//25.0
    }

    @Test
    public void maxTest() {
        compatible_num("1", "22.99", "$.store.book[*].price.max()");
        compatible_num("2", "20.0", "$.store.inventory.max()");
        compatible_num("3", "8.95", "$.store.book[?(@.category == 'reference')].price.max()");
        compatible_num("4", "5.0", "$.store.book[3].ratings.max()");
        compatible_num("5", "40.0", "$.store.staff[*].age.max()");
    }

    @Test
    public void avgTest() {
        compatible_num("1", "13.48", "$.store.book[*].price.avg()");
        compatible_num("2", "12.5", "$.store.inventory.avg()");
        compatible_num("3", "8.97", "$.store.book[?(@.price < 10)].price.avg()");
        compatible_num("4", "5.0", "$.store.book[1].ratings.avg()");
        compatible_num("5", "31.666", "$.store.staff[*].age.avg()");
    }

    @Test
    void sumTest() {
        compatible_num("1", "53.92", "$.store.book[*].price.sum()");
        compatible_num("2", "50.0", "$.store.inventory.sum()");
        compatible_num("3", "53.92", "$.totalPrice.sum()");
        compatible_num("4", "15.0", "$.store.book[?(@.author =~ /.*Waugh/)].ratings[*].sum()");
        compatible_num("5", "95.0", "$.store.staff[*].age.sum()");
    }


    private void compatible_num(String tag, String ref, String jsonpathStr) {
        System.out.println("::::" + tag + " - " + jsonpathStr);

        ONode tmp = oNode.select(jsonpathStr);
        System.out.println(tmp.toJson());

        try {
            Object tmp2 = context.read(jsonpathStr);
            System.out.println(ONode.serialize(tmp2));

            if (tmp.toJson().equals(ONode.serialize(tmp2)) == false) {
                assert Math.abs(tmp.getDouble() - Double.parseDouble(ref)) < 0.001;
            }
        } catch (Exception e) {
            System.out.println("jayway: err");
            assert Math.abs(tmp.getDouble() - Double.parseDouble(ref)) < 0.001;
        }
    }

    private void compatible_str(String tag, String ref, String jsonpathStr) {
        System.out.println("::::" + tag + " - " + jsonpathStr);

        ONode tmp = oNode.select(jsonpathStr);
        System.out.println(tmp.toJson());

        try {
            Object tmp2 = context.read(jsonpathStr);
            System.out.println(ONode.serialize(tmp2));

            if (tmp.toJson().equals(ONode.serialize(tmp2)) == false) {
                assert ref.length() == tmp.toJson().length();
            }
        } catch (Exception e) {
            System.out.println("jayway: err");
            assert ref.length() == tmp.toJson().length();
        }
    }
}