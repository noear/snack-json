package features.snack4.path.jayway.op;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Jayway JsonPath 14 种操作符的 JUnit 5 单元测试 (共 70 个测试)
 *
 * 测试基于以下 JSON 结构：
 * 包含 4 个元素，用于测试不同类型的数据：
 * 1. 数值、布尔、字符串
 * 2. 数组 (tags)
 * 3. 空数组和空字符串
 * 4. 较长字符串
 */
public class OperatorTest {

    private static String json;
    private static Configuration conf;

    @BeforeAll
    static void setUp() {
        json = "[\n" +
                "  { \"id\": 1, \"status\": \"ACTIVE\", \"count\": 100, \"available\": true, \"tags\": [\"A\", \"B\"], \"data\": \"hello world\" },\n" +
                "  { \"id\": 2, \"status\": \"INACTIVE\", \"count\": 200, \"available\": false, \"tags\": [\"C\"], \"data\": \"FOO bar\" },\n" +
                "  { \"id\": 3, \"status\": \"PENDING\", \"count\": 150, \"available\": true, \"tags\": [\"B\", \"C\", \"D\"], \"data\": \"another test\" },\n" +
                "  { \"id\": 4, \"status\": \"EXPIRED\", \"count\": 50, \"available\": false, \"tags\": [], \"data\": \"\" },\n" +
                "  { \"id\": 5, \"status\": null, \"count\": null, \"available\": null, \"tags\": null, \"data\": null }\n" +
                "]";

        // 配置：确保返回的结果是 List 而不是单个元素
        conf = Configuration.builder()
                .options(Option.ALWAYS_RETURN_LIST).build();
    }

    static Options options = Options.of(Feature.JsonPath_AlwaysReturnList);
    private List<Object> readPath(String path) {
        System.out.println("--------------------------: " + path);

        ONode rst1 = ONode.ofJson(json, options).select(path);
        System.out.println(rst1.toJson());

        List<Object> rst2 = JsonPath.using(conf).parse(json).read(path);
        System.out.println(rst2);

        if (rst1.toJson().equals(rst2.toString()) == false) {
            System.out.println("");
            System.out.println(json);
            assert false;
        }


        return rst2;
    }

    // ===================================================================
    // 1. == (等于)
    // ===================================================================

    @Test
    void test_EQ_1_StringEquality() {
        String path = "$[?(@.status == 'ACTIVE')]";
        assertEquals(1, readPath(path).size());
    }

    @Test
    void test_EQ_2_NumericEquality() {
        String path = "$[?(@.count == 200)]";
        assertEquals(1, readPath(path).size());
    }

    @Test
    void test_EQ_3_BooleanEquality() {
        String path = "$[?(@.available == true)]";
        assertEquals(2, readPath(path).size()); // id 1, 3
    }

    @Test
    void test_EQ_4_NullEquality() {
        String path = "$[?(@.status == null)]";
        assertEquals(1, readPath(path).size()); // id 5
    }

    @Test
    void test_EQ_5_EmptyStringEquality() {
        String path = "$[?(@.data == '')]";
        assertEquals(1, readPath(path).size()); // id 4
    }

    // ===================================================================
    // 2. != (不等于)
    // ===================================================================

    @Test
    void test_NE_1_StringInequality() {
        String path = "$[?(@.status != 'INACTIVE')]";
        assertEquals(4, readPath(path).size()); // 排除 id 2
    }

    @Test
    void test_NE_2_NumericInequality() {
        String path = "$[?(@.count != 100)]";
        assertEquals(4, readPath(path).size()); // 排除 id 1, 包含 id 5 (null)
    }

    @Test
    void test_NE_3_BooleanInequality() {
        String path = "$[?(@.available != false)]";
        assertEquals(3, readPath(path).size()); // 排除 id 2, 4, 包含 id 5 (null)
    }

    @Test
    void test_NE_4_NullInequality() {
        String path = "$[?(@.status != null)]";
        assertEquals(4, readPath(path).size()); // 排除 id 5
    }

    @Test
    void test_NE_5_AllMatch() {
        String path = "$[?(@.id != 99)]";
        assertEquals(5, readPath(path).size()); // 所有 id 都不等于 99
    }

    // ===================================================================
    // 3. < (小于)
    // ===================================================================

    @Test
    void test_LT_1_NumericLessThan() {
        String path = "$[?(@.count < 150)]";
        assertEquals(2, readPath(path).size()); // id 1 (100), id 4 (50)
    }

    @Test
    void test_LT_2_StrictlyLessThanBoundary() {
        String path = "$[?(@.count < 100)]";
        assertEquals(1, readPath(path).size()); // id 4 (50)
    }

    @Test
    void test_LT_3_StringLessThan() {
        // PENDING < reference
        String path = "$[?(@.status < 'PENDING')]";
        assertEquals(3, readPath(path).size()); // ACTIVE, EXPIRED (A, E)
    }

    @Test
    void test_LT_4_NoMatch() {
        String path = "$[?(@.count < 50)]";
        assertTrue(readPath(path).isEmpty());
    }

    @Test
    void test_LT_5_NullIgnored() {
        String path = "$[?(@.count < 300)]";
        assertEquals(4, readPath(path).size()); // 忽略 null 值 (id 5)
    }

    // ===================================================================
    // 4. <= (小于等于)
    // ===================================================================

    @Test
    void test_LE_1_LessThanOrEqualToBoundary() {
        String path = "$[?(@.count <= 100)]";
        assertEquals(2, readPath(path).size()); // id 1 (100), id 4 (50)
    }

    @Test
    void test_LE_2_MultipleMatches() {
        String path = "$[?(@.count <= 200)]";
        assertEquals(4, readPath(path).size()); // id 1, 2, 3, 4
    }

    @Test
    void test_LE_3_StringLessThanOrEqualTo() {
        String path = "$[?(@.status <= 'PENDING')]";
        // ACTIVE, EXPIRED, INACTIVE, PENDING
        assertEquals(4, readPath(path).size());
    }

    @Test
    void test_LE_4_NoMatch() {
        String path = "$[?(@.count <= 49)]";
        assertTrue(readPath(path).isEmpty());
    }

    @Test
    void test_LE_5_EqualityWithZero() {
        String path = "$[?(@.id <= 1)]";
        assertEquals(1, readPath(path).size()); // id 1
    }

    // ===================================================================
    // 5. > (大于)
    // ===================================================================

    @Test
    void test_GT_1_NumericGreaterThan() {
        String path = "$[?(@.count > 150)]";
        assertEquals(1, readPath(path).size()); // id 2 (200)
    }

    @Test
    void test_GT_2_StrictlyGreaterThanBoundary() {
        String path = "$[?(@.count > 100)]";
        assertEquals(2, readPath(path).size()); // id 2 (200), id 3 (150)
    }

    @Test
    void test_GT_3_StringGreaterThan() {
        String path = "$[?(@.status > 'PENDING')]";
        assertEquals(0, readPath(path).size()); // PENDING 是最大的
    }

    @Test
    void test_GT_4_AllMatch() {
        String path = "$[?(@.count > 40)]";
        assertEquals(4, readPath(path).size()); // id 1, 2, 3, 4
    }

    @Test
    void test_GT_5_NoMatch() {
        String path = "$[?(@.count > 300)]";
        assertTrue(readPath(path).isEmpty());
    }

    // ===================================================================
    // 6. >= (大于等于)
    // ===================================================================

    @Test
    void test_GE_1_GreaterThanOrEqualToBoundary() {
        String path = "$[?(@.count >= 150)]";
        assertEquals(2, readPath(path).size()); // id 2 (200), id 3 (150)
    }

    @Test
    void test_GE_2_SingleMatch() {
        String path = "$[?(@.count >= 200)]";
        assertEquals(1, readPath(path).size()); // id 2 (200)
    }

    @Test
    void test_GE_3_StringGreaterThanOrEqualTo() {
        String path = "$[?(@.status >= 'INACTIVE')]";
        // INACTIVE, PENDING
        assertEquals(2, readPath(path).size());
    }

    @Test
    void test_GE_4_NoMatch() {
        String path = "$[?(@.count >= 201)]";
        assertTrue(readPath(path).isEmpty());
    }

    @Test
    void test_GE_5_UsingAnotherField() {
        String path = "$[?(@.id >= 4)]";
        assertEquals(2, readPath(path).size()); // id 4, 5
    }

    // ===================================================================
    // 7. =~ (正则表达式匹配)
    // ===================================================================

    @Test
    void test_REGEX_1_StartsWith() {
        String path = "$[?(@.status =~ /^ACT.*/)]";
        assertEquals(1, readPath(path).size()); // ACTIVE
    }

    @Test
    void test_REGEX_2_ContainsWord() {
        String path = "$[?(@.data =~ /.*world.*/)]";
        assertEquals(1, readPath(path).size()); // "hello world"
    }

    @Test
    void test_REGEX_3_CaseInsensitive() {
        // 使用 (?i) 开启不区分大小写
        String path = "$[?(@.data =~ /(?i)foo.*/)]";
        assertEquals(1, readPath(path).size()); // "FOO bar"
    }

    @Test
    void test_REGEX_4_OrCondition() {
        String path = "$[?(@.status =~ /(ACTIVE|EXPIRED)/)]";
        assertEquals(2, readPath(path).size()); // ACTIVE, EXPIRED
    }

    @Test
    void test_REGEX_5_NoMatch() {
        String path = "$[?(@.status =~ /unknown/)]";
        assertTrue(readPath(path).isEmpty());
    }

    // ===================================================================
    // 8. in (成员关系)
    // ===================================================================

    @Test
    void test_IN_1_SingleMatch_String() {
        String path = "$[?(@.status in ['ACTIVE'])]";
        assertEquals(1, readPath(path).size());
    }

    @Test
    void test_IN_2_MultipleMatches_Numeric() {
        String path = "$[?(@.id in [1, 3])]";
        assertEquals(2, readPath(path).size()); // id 1, 3
    }

    @Test
    void test_IN_3_AllMatch() {
        String path = "$[?(@.status in ['ACTIVE', 'INACTIVE', 'PENDING', 'EXPIRED', null])]";
        assertEquals(5, readPath(path).size()); // 匹配所有状态，包括 null
    }

    @Test
    void test_IN_4_NoMatch() {
        String path = "$[?(@.status in ['COMPLETED'])]";
        assertTrue(readPath(path).isEmpty());
    }

    @Test
    void test_IN_5_BooleanIn() {
        String path = "$[?(@.available in [false])]";
        assertEquals(2, readPath(path).size()); // id 2, 4
    }

    // ===================================================================
    // 9. nin (非成员关系)
    // ===================================================================

    @Test
    void test_NIN_1_SingleExclusion() {
        String path = "$[?(@.status nin ['ACTIVE'])]";
        assertEquals(4, readPath(path).size()); // 排除 id 1
    }

    @Test
    void test_NIN_2_MultipleExclusions() {
        String path = "$[?(@.id nin [1, 5])]";
        assertEquals(3, readPath(path).size()); // 排除 id 1, 5
    }

    @Test
    void test_NIN_3_EmptyList() {
        String path = "$[?(@.status nin [])]";
        // 所有状态都不在空列表中 (JsonPath 认为这是合法的，即所有元素都满足)
        assertEquals(5, readPath(path).size());
    }

    @Test
    void test_NIN_4_ExcludingNull() {
        String path = "$[?(@.status nin [null])]";
        assertEquals(4, readPath(path).size()); // 排除 id 5
    }

    @Test
    void test_NIN_5_NumericExclusion() {
        String path = "$[?(@.count nin [100, 200, 150, 50])]";
        assertEquals(1, readPath(path).size()); // 只剩 id 5 (null)
    }

    // ===================================================================
    // 10. subsetof (左是右的子集)
    // ===================================================================

    @Test
    void test_SUBSETOF_1_ExactMatch() {
        String path = "$[?(@.tags subsetof ['A', 'B'])]";
        assertEquals(2, readPath(path).size()); // id 1 (["A", "B"])
    }

    @Test
    void test_SUBSETOF_2_ProperSubset() {
        String path = "$[?(@.tags subsetof ['A', 'B', 'C', 'D'])]";
        assertEquals(4, readPath(path).size()); // id 1, 2, 3, 4 (空集是任何集合的子集)
    }

    @Test
    void test_SUBSETOF_3_EmptySet() {
        String path = "$[?(@.tags subsetof ['X', 'Y'])]";
        assertEquals(1, readPath(path).size()); // id 4 (空数组 [] 是任何集合的子集)
    }

    @Test
    void test_SUBSETOF_4_NotSubset() {
        String path = "$[?(@.tags subsetof ['A'])]";
        assertFalse(readPath(path).isEmpty()); // id 1 (["A", "B"]) 不匹配
    }

    @Test
    void test_SUBSETOF_5_NullIgnored() {
        String path = "$[?(@.tags subsetof ['A', 'B'])]";
        // id 5 (null) 被忽略
        assertEquals(2, readPath(path).size());
    }

    // ===================================================================
    // 11. anyof (左与右有一个交点)
    // ===================================================================

    @Test
    void test_ANYOF_1_SingleIntersection() {
        String path = "$[?(@.tags anyof ['A'])]";
        assertEquals(1, readPath(path).size()); // id 1 (["A", "B"])
    }

    @Test
    void test_ANYOF_2_MultipleIntersections() {
        String path = "$[?(@.tags anyof ['B', 'C'])]";
        assertEquals(3, readPath(path).size()); // id 1 (B), id 2 (C), id 3 (B, C, D)
    }

    @Test
    void test_ANYOF_3_NoIntersection() {
        String path = "$[?(@.tags anyof ['X', 'Y'])]";
        assertTrue(readPath(path).isEmpty()); // id 1, 2, 3 不匹配。id 4 是空集，无交集。id 5 是 null
    }

    @Test
    void test_ANYOF_4_EmptyTargetSet() {
        String path = "$[?(@.tags anyof [])]";
        assertTrue(readPath(path).isEmpty()); // 与空集没有交集
    }

    @Test
    void test_ANYOF_5_SourceEmptyArray() {
        String path = "$[?(@.tags anyof ['A', 'B'])]";
        // id 4 (空数组) 与 ['A', 'B'] 没有交集
        assertEquals(2, readPath(path).size());
    }

    // ===================================================================
    // 12. noneof (左与右没有交集)
    // ===================================================================

    @Test
    void test_NONEOF_1_NoIntersectionMatch() {
        String path = "$[?(@.tags noneof ['X', 'Y'])]";
        assertEquals(4, readPath(path).size()); // id 4 (空数组)
    }

    @Test
    void test_NONEOF_2_PartialMatch() {
        String path = "$[?(@.tags noneof ['A', 'C'])]";
        // id 1 (A), id 2 (C), id 3 (C) 不匹配。id 4 匹配
        assertEquals(1, readPath(path).size());
    }

    @Test
    void test_NONEOF_3_FullOverlap() {
        String path = "$[?(@.tags noneof ['A', 'B', 'C', 'D'])]";
        // id 1, 2, 3 都有交集，不匹配。id 4 (空数组) 匹配
        assertEquals(1, readPath(path).size());
    }

    @Test
    void test_NONEOF_4_EmptyTargetSet() {
        String path = "$[?(@.tags noneof [])]";
        // 与空集没有交集，因此所有非 null 数组都满足
        assertEquals(4, readPath(path).size()); // id 1, 2, 3, 4
    }

    @Test
    void test_NONEOF_5_NonArrayField() {
        String path = "$[?(@.status noneof ['A', 'B'])]";
        // status 不是数组，此操作符应该跳过或视为不匹配
        assertTrue(readPath(path).isEmpty());
    }

    // ===================================================================
    // 13. size (左的大小应该与右匹配)
    // ===================================================================

    @Test
    void test_SIZE_1_ArraySizeMatch() {
        String path = "$[?(@.tags size 2)]";
        assertEquals(1, readPath(path).size()); // id 1 (["A", "B"]), id 3 (["B", "C", "D"]) 不匹配
    }

    @Test
    void test_SIZE_2_ZeroSize() {
        String path = "$[?(@.tags size 0)]";
        assertEquals(1, readPath(path).size()); // id 4 ([])
    }

    @Test
    void test_SIZE_3_StringSizeMatch() {
        String path = "$[?(@.data size 0)]";
        assertEquals(1, readPath(path).size()); // id 4 ("")
    }

    @Test
    void test_SIZE_4_StringSizeNonZero() {
        String path = "$[?(@.data size 12)]";
        assertEquals(1, readPath(path).size()); // id 1 ("hello world")
    }

    @Test
    void test_SIZE_5_NoMatch() {
        String path = "$[?(@.tags size 4)]";
        assertTrue(readPath(path).isEmpty());
    }

    // ===================================================================
    // 14. empty (Left（数组或字符串）应该为空)
    // ===================================================================

    @Test
    void test_EMPTY_1_True_EmptyArray() {
        String path = "$[?(@.tags empty true)]";
        assertEquals(1, readPath(path).size()); // id 4 ([])
    }

    @Test
    void test_EMPTY_2_True_EmptyString() {
        String path = "$[?(@.data empty true)]";
        assertEquals(1, readPath(path).size()); // id 4 ("")
    }

    @Test
    void test_EMPTY_3_False_NonEmptyArray() {
        String path = "$[?(@.tags empty false)]";
        assertEquals(3, readPath(path).size()); // id 1, 2, 3 (非空数组)
    }

    @Test
    void test_EMPTY_4_False_NonEmptyString() {
        String path = "$[?(@.data empty false)]";
        assertEquals(3, readPath(path).size()); // id 1, 2, 3 (非空字符串)
    }

    @Test
    void test_EMPTY_5_NullIgnored() {
        String path = "$[?(@.tags empty true)]";
        // id 5 (null) 被忽略
        assertEquals(1, readPath(path).size());
    }
}