package features.path.generated;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.JsonPath;

import static org.junit.jupiter.api.Assertions.*;

public class JsonPathSelectComplexTest2 {

    // 测试数据模板
    private static final String BOOKS_JSON = "{\n" +
            "          \"store\": {\n" +
            "            \"book\": [\n" +
            "              { \"category\": \"fiction\", \"title\": \"Book A\", \"price\": 8.95, \"author\": \"Smith\", \"tags\": [\"sci-fi\", \"action\"], \"stock\": 5 },\n" +
            "              { \"category\": \"tech\", \"title\": \"Book B\", \"price\": 29.99, \"author\": \"Johnson\", \"tags\": [\"programming\", \"java\"], \"stock\": 0 },\n" +
            "              { \"category\": \"fiction\", \"title\": \"Book C\", \"price\": 12.50, \"author\": \"Williams\", \"tags\": [\"fantasy\", \"magic\"], \"stock\": 3 },\n" +
            "              { \"category\": \"history\", \"title\": \"Book D\", \"price\": 35.00, \"author\": \"Brown\", \"tags\": [\"world war\", \"non-fiction\"], \"stock\": 2 },\n" +
            "              { \"category\": \"tech\", \"title\": \"Book E\", \"price\": 49.99, \"author\": \"Davis\", \"tags\": [\"cloud\", \"devops\"], \"stock\": 7 }\n" +
            "            ]\n" +
            "          }\n" +
            "        }";

    private ONode loadRoot() {
        return ONode.load(BOOKS_JSON);
    }

    @Test
    void testMultiCondition_PriceAndCategory() {
        ONode result = JsonPath.select(loadRoot(),
                "$.store.book[?(@.price > 10 && @.price < 30 && @.category == 'fiction')]");

        assertEquals(1, result.size());
        assertEquals("Book C", result.get(0).get("title").getString());
    }

    @Test
    void testNestedOrConditions() {
        ONode result = JsonPath.select(loadRoot(),
                "$.store.book[?(@.price < 10 || @.price > 40)]");

        assertEquals(2, result.size());
        assertArrayEquals(new String[]{"Book A", "Book E"},
                result.getArray().stream().map(n -> n.get("title").getString()).toArray());
    }

    @Test
    void testContainsWithLogic() {
        ONode result = JsonPath.select(loadRoot(),
                "$.store.book[?(@.tags contains 'magic' && @.stock > 0)]");

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).get("stock").getInt());
    }

    @Test
    void testNestedPropertyCheck() {
        ONode result = JsonPath.select(loadRoot(),
                "$.store.book[?(@.author startsWith 'W' && @.price <= 15)]");

        assertEquals(1, result.size());
        assertEquals("Williams", result.get(0).get("author").getString());
    }

    @Test
    void testComplexParenthesis() {
        ONode result = JsonPath.select(loadRoot(),
                "$.store.book[?((@.category == 'tech' || @.category == 'history') && @.price > 20)]");

        assertEquals(3, result.size());
        assertTrue(result.select("$[*].title").toJson().contains("Book B"));
        assertTrue(result.select("$[*].title").toJson().contains("Book D"));
        assertTrue(result.select("$[*].title").toJson().contains("Book E"));
    }

    @Test
    void testInOperatorCombination() {
        ONode result = JsonPath.select(loadRoot(),
                "$.store.book[?(@.tags in ['sci-fi','cloud'] && @.stock != 0)]");

        assertEquals(2, result.size());
        assertArrayEquals(new String[]{"Book A", "Book E"},
                result.select("$[*].title").toBean(String[].class));
    }

    @Test
    void testDeepNestedConditions() {
        ONode result = JsonPath.select(loadRoot(),
                "$.store.book[?((@.price < 30 && @.stock > 0) || (@.author contains 'son'))]");

        assertEquals(3, result.size());
        assertTrue(result.select("$[*].title").toJson().contains("Book A"));
        assertTrue(result.select("$[*].title").toJson().contains("Book C"));
        assertTrue(result.select("$[*].title").toJson().contains("Book B"));
    }

    @Test
    void testMultipleRangeChecks() {
        ONode result = JsonPath.select(loadRoot(),
                "$.store.book[?(@.price >= 10 && @.price <= 30 && @.stock < 5)]");

        assertEquals(2, result.size());
        assertArrayEquals(new String[]{"Book B", "Book C"},
                result.select("$[*].title").toBean(String[].class));
    }

    @Test
    void testNegatedConditions() {
        ONode result = JsonPath.select(loadRoot(),
                "$.store.book[?(!(@.category == 'fiction') && @.price < 40)]");

        assertEquals(2, result.size());
        assertTrue(result.select("$[*].category").toJson().contains("fiction"));
    }

    @Test
    void testMixedOperatorsPriority() {
        ONode result = JsonPath.select(loadRoot(),
                "$.store.book[?(@.price > 20 || @.stock > 3 && @.category == 'tech')]");

        assertEquals(3, result.size());
        assertTrue(result.select("$[*].title").toJson().contains("Book B"));
        assertTrue(result.select("$[*].title").toJson().contains("Book D"));
        assertTrue(result.select("$[*].title").toJson().contains("Book E"));
    }

    @Test
    void testStringOperationsCombination() {
        ONode result = JsonPath.select(loadRoot(),
                "$.store.book[?((@.title startsWith 'Book' && @.title endsWith 'B') || @.author == 'Brown')]");

        assertEquals(2, result.size());
        assertArrayEquals(new String[]{"Book B", "Book D"},
                result.select("$[*].title").toBean(String[].class));
    }

    @Test
    void testComplexArrayContains() {
        ONode result = JsonPath.select(loadRoot(),
                "$.store.book[?(@.tags contains 'non-fiction' && @.tags contains 'world war')]");

        assertEquals(1, result.size());
        assertEquals("Book D", result.get(0).get("title").getString());
    }

    @Test
    void testNestedJsonPathInFilter() {
        ONode result = JsonPath.select(loadRoot(),
                "$.store.book[?(@.tags[0] == 'sci-fi' || @.tags[1] == 'devops')]");

        assertEquals(2, result.size());
        assertArrayEquals(new String[]{"Book A", "Book E"},
                result.select("$[*].title").toBean(String[].class));
    }

    @Test
    void testMultipleValueRanges() {
        ONode result = JsonPath.select(loadRoot(),
                "$.store.book[?((@.price >= 10 && @.price <= 20) || (@.price >= 30 && @.price <= 50))]");

        assertEquals(3, result.size());
        assertArrayEquals(new String[]{"Book C", "Book D", "Book E"},
                result.select("$[*].title").toBean(String[].class));
    }

    @Test
    void testComplexNullSafety() {
        ONode result = JsonPath.select(loadRoot(),
                "$.store.book[?(@.discount != null && @.discount.rate > 0.1)]");

        assertEquals(0, result.size()); // 没有discount字段
    }

    @Test
    void testMultipleIndexAccess() {
        ONode result = JsonPath.select(loadRoot(),
                "$.store.book[?(@.tags[0] in ['programming','fantasy'] && @.tags[1] != 'java')]");

        assertEquals(1, result.size());
        assertArrayEquals(new String[]{"Book C"},
                result.select("$[*].title").toBean(String[].class));
    }

    @Test
    void testComplexRegexMatching() {
        ONode result = JsonPath.select(loadRoot(),
                "$.store.book[?(@.title =~ /^Book\\s[A-D]$/ && @.price < 30)]");

        assertEquals(3, result.size());
        assertArrayEquals(new String[]{"Book A", "Book B", "Book C"},
                result.select("$[*].title").toBean(String[].class));
    }

    @Test
    void testDeepRecursiveFilter() {
        ONode result = JsonPath.select(loadRoot(),
                "$..book[?(@.price < 40 && (@.category == 'tech' || @.author contains 'son'))]");

        assertEquals(1, result.size());
        assertArrayEquals(new String[]{"Book B"},
                result.select("$[*].title").toBean(String[].class));
    }
}