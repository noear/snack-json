package features.snack4.codec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.noear.snack4.json.util.NameUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JUnit 5 (Java 8+) 测试 NameUtil 中的命名转换方法。
 */
class NameUtilTest {

    // 共享的 StringBuilder 实例，用于测试方法。
    // 因为 NameUtil 的方法需要它作为第一个参数。
    private StringBuilder sharedBuffer;

    @BeforeEach
    void setUp() {
        // 在每个测试方法运行前初始化或清空 StringBuilder
        sharedBuffer = new StringBuilder();
    }

    // --- toSmlSnakeStyle 测试 (驼峰 -> 小蛇) ---

    @DisplayName("测试 toSmlSnakeStyle 基础转换 (驼峰转小蛇)")
    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            // 小驼峰转小蛇 (标准情况)
            "userName, user_name",
            "firstNameAndLastName, first_name_and_last_name",
            "productId, product_id",
            // 大驼峰转小蛇 (标准情况)
            "UserName, user_name",
            "ProductName, product_name",
            // 混合大小写（函数会转为小写）
            "TestName, test_name",
            // 包含数字
            "user1Id, user1_id",
            "version2023, version2023",
            // 连续大写（函数会按每个大写字母处理）
            "URLDecoder, url_decoder",
            "ProductID, product_id", // 边界案例：ID 连续大写
    })
    void testToSmlSnakeStyle_StandardCases(String originName, String expected) {
        String result = NameUtil.toSmlSnakeStyle(sharedBuffer, originName);
        assertEquals(expected, result);
        // 检查 buf 是否被清空并正确使用
        assertEquals(expected, sharedBuffer.toString());
    }

    @DisplayName("测试 toSmlSnakeStyle 同风格及边界情况")
    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            // 同风格 (小蛇保持不变)
            "user_name, user_name",
            "first_name_and_last_name, first_name_and_last_name",
            // 混合风格 (函数只处理大写字母)
            "USER_ID, user_id", // 连续大写被拆分
            "USER_name, user_name",
            // 边界情况
            "a, a", // 单字符
            "A, a", // 单大写字符
            "_test, _test", // 以 _ 开头，保持不变
            "test_, test_", // 以 _ 结尾，保持不变
    })
    void testToSmlSnakeStyle_BoundaryAndSameStyle(String originName, String expected) {
        String result = NameUtil.toSmlSnakeStyle(sharedBuffer, originName);
        assertEquals(expected, result);
    }

    @DisplayName("测试 toSmlSnakeStyle 空值/空字符串")
    @ParameterizedTest
    @NullAndEmptySource
    void testToSmlSnakeStyle_NullOrEmpty(String originName) {
        String result = NameUtil.toSmlSnakeStyle(sharedBuffer, originName);
        assertEquals(originName, result); // 期望返回原样
    }

    // --- toSmlCamelStyle 测试 (小蛇 -> 小驼峰) ---

    @DisplayName("测试 toSmlCamelStyle 基础转换 (小蛇转小驼峰)")
    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            // 小蛇转小驼峰 (标准情况)
            "user_name, userName",
            "first_name_and_last_name, firstNameAndLastName",
            "product_id, productId",
            // 包含数字
            "user_1_id, user1Id",
            "version_2023, version2023",
            // 混合大小写输入 (函数会预先转小写)
            "USER_NAME, userName",
            "Mixed_CASE_Test, mixedCaseTest",
            "snake_STYLE, snakeStyle"
    })
    void testToSmlCamelStyle_StandardCases(String originName, String expected) {
        String result = NameUtil.toSmlCamelStyle(sharedBuffer, originName);
        assertEquals(expected, result);
        // 检查 buf 是否被清空并正确使用
        assertEquals(expected, sharedBuffer.toString());
    }

    @DisplayName("测试 toSmlCamelStyle 同风格及边界情况")
    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            // 同风格 (小驼峰保持不变)
            "userName, userName",
            "id, id",
            // 无下划线输入
            "user, user",
            // 边界情况
            "a_b_c, aBC", // 多个转换
            "a_b_, aB", // 以 _ 结尾，被忽略
            "_a_b, aB", // 以 _ 开头，被忽略
            "__a_b, aB", // 连续下划线，被视为一个分隔符
            "a, a",
    })
    void testToSmlCamelStyle_BoundaryAndSameStyle(String originName, String expected) {
        // 由于 toSmlCamelStyle 遇到没有 '_' 的字符串会直接返回，
        // 且它在内部将 originName.toLowerCase() 赋值给了 originName，
        // 但原始代码中没有这一行：`originName = originName.toLowerCase();`
        // 实际函数逻辑：如果 originName 不包含 '_', 则直接返回原始 originName。
        // 如果包含 '_'，则对所有字符进行转换，相当于先 toLowerCase() 再处理。

        // 针对 "userName"：不含 '_'，直接返回 "userName"
        // 针对 "id"：不含 '_'，直接返回 "id"
        String result = NameUtil.toSmlCamelStyle(sharedBuffer, originName);
        assertEquals(expected, result);
    }

    @DisplayName("测试 toSmlCamelStyle 空值/空字符串")
    @ParameterizedTest
    @NullAndEmptySource
    void testToSmlCamelStyle_NullOrEmpty(String originName) {
        String result = NameUtil.toSmlCamelStyle(sharedBuffer, originName);
        assertEquals(originName, result); // 期望返回原样
    }
}