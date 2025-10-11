package features.snack4.path.generated;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.noear.snack4.jsonpath.segment.SelectSegment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SplitSelectorsTest {

    /**
     * 提供参数化测试的数据源
     * 每个 Arguments 对象包含：1. 测试名称, 2. 输入字符串, 3. 期望的 List<String> 结果
     */
    static Stream<Arguments> selectorTestCases() {
        return Stream.of(
                // --- 基础场景 ---
                Arguments.of("Case 1: 简单的多索引",
                        "1,2,5",
                        Arrays.asList("1", "2", "5")),

                Arguments.of("Case 2: 简单的多名称选择器",
                        "'name','age','address'",
                        Arrays.asList("'name'", "'age'", "'address'")),

                Arguments.of("Case 3: 单个选择器，不应分割",
                        "1:5",
                        Arrays.asList("1:5")),

                // --- 关键场景：逗号在括号内 ---
                Arguments.of("Case 4: 逗号在过滤器 'in' 的方括号内",
                        "?(@.category in ['books', 'fiction'])",
                        Arrays.asList("?(@.category in ['books', 'fiction'])")),

                Arguments.of("Case 5: 逗号在脚本表达式的圆括号内",
                        "?(@.name.matches('a,b'))",
                        Arrays.asList("?(@.name.matches('a,b'))")),

                Arguments.of("Case 6: 你的问题案例 - 过滤器中的数组",
                        "?@a in [1,2]",
                        Arrays.asList("?@a in [1,2]")),

                // --- 混合场景 ---
                Arguments.of("Case 7: 简单选择器与复杂过滤器的混合",
                        "0, ?(@.price < 10 && @.isbn), 'title'",
                        Arrays.asList("0", "?(@.price < 10 && @.isbn)", "'title'")),

                Arguments.of("Case 8: 多切片与多索引的混合",
                        "1, 5, 10:20, 30",
                        Arrays.asList("1", "5", "10:20", "30")),

                // --- 边缘场景 ---
                Arguments.of("Case 9: 输入为空字符串",
                        "",
                        Arrays.asList("")),

                Arguments.of("Case 10: 包含大量空格",
                        " 1 ,  'name'  , [?(@.id==1)] ",
                        Arrays.asList("1", "'name'", "[?(@.id==1)]")),

                Arguments.of("Case 11: 你的另一个问题案例 - 过滤器联合",
                        "?@a > 1, ?@b > 1",
                        Arrays.asList("?@a > 1", "?@b > 1")),

                Arguments.of("Case 12: 嵌套的方括号和圆括号",
                        "?(@.data[0].values(1,2) > 5)",
                        Arrays.asList("?(@.data[0].values(1,2) > 5)"))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("selectorTestCases")
    @DisplayName("测试各种场景下的选择器分割")
    void testSplitSelectors(String testName, String input, List<String> expected) {
        List<String> actual = SelectSegment.splitSelectors(input);
        assertEquals(expected, actual);
    }

    // --- 补充的独立测试用例 ---

    @Test
    @DisplayName("Case 13: 测试带有前导和尾随逗号的字符串")
    void testSplitWithLeadingAndTrailingCommas() {
        String input = ",1,2,";
        List<String> expected = Arrays.asList("", "1", "2", "");
        List<String> actual = SelectSegment.splitSelectors(input);
        assertEquals(expected, actual, "应正确处理前导和尾随逗号");
    }

    @Test
    @DisplayName("Case 14: 测试只有逗号的字符串")
    void testSplitWithOnlyCommas() {
        String input = ",,";
        List<String> expected = Arrays.asList("", "", "");
        List<String> actual = SelectSegment.splitSelectors(input);
        assertEquals(expected, actual, "应将只有逗号的字符串分割为空字符串数组");
    }
}