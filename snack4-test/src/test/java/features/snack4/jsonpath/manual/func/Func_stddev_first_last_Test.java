package features.snack4.jsonpath.manual.func;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.util.MathUtil;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author noear 2025/10/12 created
 *
 */
public class Func_stddev_first_last_Test {
    //不开启 jayway 特性
    private static String JSON_DATA = "{\n" +
            "  \"store\": {\n" +
            "    \"book\": [\n" +
            "      {\n" +
            "        \"category\": \"reference\",\n" +
            "        \"author\": \"Nigel Rees\",\n" +
            "        \"title\": \"Sayings of the Century\",\n" +
            "        \"price\": 8.95,\n" +
            "        \"ratings\": [4, 5, 4]\n" +
            "      },\n" +
            "      {\n" +
            "        \"category\": \"fiction\",\n" +
            "        \"author\": \"Evelyn Waugh\",\n" +
            "        \"title\": \"Sword of Honour\",\n" +
            "        \"price\": 12.99,\n" +
            "        \"ratings\": [5, 5, 5]\n" +
            "      },\n" +
            "      {\n" +
            "        \"category\": \"fiction\",\n" +
            "        \"author\": \"Herman Melville\",\n" +
            "        \"title\": \"Moby Dick\",\n" +
            "        \"isbn\": \"0-553-21311-3\",\n" +
            "        \"price\": 8.99,\n" +
            "        \"ratings\": [3, 4, 3, 5]\n" +
            "      },\n" +
            "      {\n" +
            "        \"category\": \"fiction\",\n" +
            "        \"author\": \"J. R. R. Tolkien\",\n" +
            "        \"title\": \"The Lord of the Rings\",\n" +
            "        \"isbn\": \"0-395-19395-8\",\n" +
            "        \"price\": 22.99,\n" +
            "        \"ratings\": [5, 5, 5, 5, 5]\n" +
            "      }\n" +
            "    ],\n" +
            "    \"bicycle\": {\n" +
            "      \"color\": \"red\",\n" +
            "      \"price\": 19.95,\n" +
            "      \"stock\": 10\n" +
            "    },\n" +
            "    \"staff\": [\n" +
            "      { \"name\": \"Alice\", \"age\": 30 },\n" +
            "      { \"name\": \"Bob\", \"age\": 25 },\n" +
            "      { \"name\": \"Charlie\", \"age\": 40 }\n" +
            "    ],\n" +
            "    \"inventory\": [10, 5, 20, 15]\n" +
            "  },\n" +
            "  \"totalPrice\": 53.92\n" +
            "}";

    private static DocumentContext context;
    private static ONode oNode;

    @BeforeAll
    static void setup() {
        context = JsonPath.parse(JSON_DATA);
        oNode = ONode.ofJson(JSON_DATA);
    }

    @Test
    public void stddevCheck0() {
        List<Double> data = Arrays.asList(600D, 470D, 170D, 430D, 300D);

        System.out.println("数据数组: " + data);

        double mean = MathUtil.calculateMean(data);
        System.out.printf("平均值 (Mean): %.2f%n", mean);

        assert Math.abs(394.00D - mean) < 0.01;

        double stdDev = MathUtil.calculateStdDev(data);
        System.out.printf("标准差 (StdDev): %.2f%n", stdDev);

        assert Math.abs(147.32D - stdDev) < 0.01;

        // 示例结果:
        // 平均值 (Mean): 394.00
        // 标准差 (StdDev): 147.32 (约等于)
    }

    @Test
    public void stddevCheck1(){
        List<Double> data = Arrays.asList(30D,25D,40D);
        double stdDev = MathUtil.calculateStdDev(data);
        System.out.printf("标准差 (StdDev): %f", stdDev);
    }

    @Test
    public void stddevTest(){
        compatible_str("1","xxx","$.store.book[*].price");
        compatible_num("1","5.730646","$.store.book[*].price.stddev()");

        compatible_str("2","xxx","$.store.inventory");
        compatible_num("2","5.590170","$.store.inventory.stddev()");

        compatible_str("3","xxx","$.store.book[0].ratings");
        compatible_num("3","0.471405","$.store.book[0].ratings.stddev()");

        compatible_str("4","xxx","$.store.book[2].ratings");
        compatible_num("4","0.82915","$.store.book[2].ratings.stddev()");

        compatible_str("5","xxx","$.store.staff[*].age");
        compatible_num("5","6.236096","$.store.staff[*].age.stddev()");
    }

    @Test
    public void firstTest(){
        compatible_str("1","","$.store.book");
        compatible_str("1","","$.store.book.first()");
        compatible_str("2","10","$.store.inventory.first()");
        compatible_str("3","\"Alice\"","$.store.staff[*].name.first()");
        compatible_str("4","3","$.store.book[2].ratings.first()");
        compatible_str("5","\"Nigel Rees\"","$.store.book[:2].first().author");
    }

    @Test
    public void lastTest(){
        compatible_str("1","xxx","$.store.book");
        compatible_str("1","xxx","$.store.book.last()");
        compatible_str("2","15","$.store.inventory.last()");

        compatible_str("3","\"Charlie\"","$.store.staff[*].name");
        compatible_str("3","\"Charlie\"","$.store.staff[*].name.last()");

        compatible_str("4","5","$.store.book[2].ratings.last()");
        compatible_str("5","\"The Lord of the Rings\"","$.store.book[?(@.category == 'fiction')].last().title");
    }

    private void compatible_num(String tag, String ref, String jsonpathStr) {
        System.out.println("::::" + tag + " - " + jsonpathStr);

        ONode tmp = oNode.select(jsonpathStr);
        System.out.println(tmp.toJson());

        try {
            Object tmp2 = context.read(jsonpathStr);
            System.out.println(ONode.ofBean(tmp2).toJson());

            if (tmp.toJson().equals(ONode.ofBean(tmp2).toJson()) == false) {
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
            System.out.println(ONode.ofBean(tmp2).toJson());

            if (tmp.toJson().equals(ONode.ofBean(tmp2).toJson()) == false) {
                assert ref.length() == tmp.toJson().length();
            }
        } catch (Exception e) {
            System.out.println("jayway: err");
            assert ref.length() == tmp.toJson().length();
        }
    }
}
