package features.snack4.jsonpath.jayway.func;

import org.junit.jupiter.api.Test;

public class Func_min_max_avg_sum_Test extends FuncTestAbs {
    //开启 jayway 特性
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

    @Override
    protected String JSON_DATA() {
        return JSON_DATA;
    }

    @Test
    public void minTest() {
        compatible_str("1", "8.95", "$.store.book[*].price"); //8.95
        compatible_str("1", "8.95", "$.store.book[*].price.min()"); //8.95

        compatible_str("2", "5.0", "$.store.inventory.min()"); //5.0
        compatible_str("3", "8.99", "$.store.book[?(@.category == 'fiction')].price.min()");//8.99

        compatible_str("4", "3.0", "$..ratings[*]"); //3.0
        compatible_str("4", "3.0", "$..ratings[*].min()"); //3.0

        compatible_str("5", "3.0", "$.store.book[2].ratings.min()");
        compatible_str("6", "25.0", "$.store.staff[*].age.min()");//25.0
    }

    @Test
    public void maxTest() {
        compatible_str("1", "22.99", "$.store.book[*].price.max()");
        compatible_str("2", "20.0", "$.store.inventory.max()");
        compatible_str("3", "8.95", "$.store.book[?(@.category == 'reference')].price.max()");
        compatible_str("4", "5.0", "$.store.book[3].ratings.max()");
        compatible_str("5", "40.0", "$.store.staff[*].age.max()");
    }

    @Test
    public void avgTest() {
        compatible_str("1", "13.48", "$.store.book[*].price");
        compatible_str("1", "13.48", "$.store.book[*].price.avg()");

        compatible_str("2", "12.5", "$.store.inventory");
        compatible_str("2", "12.5", "$.store.inventory.avg()");

        compatible_str("3", "xxx", "$.store.book[?(@.price < 10)].price");
        compatible_num("3", "8.97", "$.store.book[?(@.price < 10)].price.avg()");

        compatible_str("4", "5.0", "$.store.book[1].ratings.avg()");

        compatible_str("5", "31.666", "$.store.staff[*].age");
        compatible_num("5", "31.666", "$.store.staff[*].age.avg()");
    }

    @Test
    void sumTest() {
        compatible_str("1", "53.92", "$.store.book[*].price.sum()");
        compatible_str("2", "50.0", "$.store.inventory.sum()");
        compatible_str("3", "53.92", "$.totalPrice.sum()");
        compatible_str("4", "15.0", "$.store.book[?(@.author =~ /.*Waugh/)].ratings[*].sum()");
        compatible_str("5", "95.0", "$.store.staff[*].age.sum()");
    }
}