package features.snack4.path.jayway.func;

import org.junit.jupiter.api.Test;

/**
 *
 * @author noear 2025/10/12 created
 *
 */
public class Func_concat_append_index_Test_no extends FuncTestAbs {
    //开启 jayway 特性
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

    @Override
    protected String JSON_DATA() {
        return JSON_DATA;
    }

    @Test
    public void indexTest() {
        compatible_str("1", "xxx", "$.store.book.index(1)"); //第二个 Book 对象 (Sword of Honour)
        compatible_str("2", "10", "$.store.inventory.index(0)");

        compatible_str("3", "\"Charlie\"", "$.store.staff[*].name");
        compatible_str("3", "\"Charlie\"", "$.store.staff[*].name.index(-1)");

        compatible_str("4", "5", "$.store.book[3].ratings");
        compatible_str("4", "5", "$.store.book[3].ratings.index(2)");

        //compatible_str("5","\"Herman Melville\"","$.store.book.index($.store.book.length() - 2).author");
    }

    @Test
    public void concatTest() {
        compatible_str("1", "[10,5,20,15]", "$.store.inventory");
        compatible_str("1", "[10,5,20,15,50]", "$.store.inventory.concat(50)");

        compatible_str("2", "[4,5,4,1]", "$.store.book[0].ratings.concat(1)");

        compatible_str("3", "[\"Alice\",\"Bob\",\"Charlie\",\"David\"]", "$.store.staff[*].name");

        //["AliceDavid","BobDavid","CharlieDavid"]
        //["","",""] jayway
        //compatible_str("3", "[\"Alice\",\"Bob\",\"Charlie\",\"David\"]", "$.store.staff[*].name.concat('David')");

        compatible_str("4", "[8.95,8.99,9.0]", "$.store.book[?(@.price < 10)].price");
        compatible_str("4", "[8.95,8.99,9.0]", "$.store.book[?(@.price < 10)].price.concat(9.00)");

        compatible_str("5", "[\"Sayings of the Century\"]", "$.store.book[?(@.category == 'reference')].title");

        //["Sayings of the Century New Ref"]
        //[""]
        //compatible_str("5", "\"Sayings of the Century New Ref\"", "$.store.book[?(@.category == 'reference')].title.concat(' New Ref')");
    }

    @Test
    public void appendTest() {
        compatible_str("1", "[10,5,20,15,100]", "$.store.inventory");
        compatible_str("1", "[10,5,20,15,100]", "$.store.inventory.append(100)");

        compatible_str("2", "[5,5,5,10]", "$.store.book[1].ratings");
        compatible_str("2", "[5,5,5,10]", "$.store.book[1].ratings.append(10)");

        compatible_str("3", "[30,25,40,50]", "$.store.staff[*].age");
        compatible_str("3", "[30,25,40,50]", "$.store.staff[*].age.append(50)");

        compatible_str("4", "xxx", "$.store.book"); //5个 Book 对象的列表 (最后一个是追加的对象)
        compatible_str("4", "xxx", "$.store.book.append({'category': 'new', 'price': 1.00})"); //5个 Book 对象的列表 (最后一个是追加的对象)

        compatible_str("5", "[5,5,5,5,5,2]", "$.store.book[?(@.price > 20)].ratings");
        compatible_str("5", "[5,5,5,5,5,2]", "$.store.book[?(@.price > 20)].ratings.append(2)");
    }
}