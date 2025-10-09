package features.snack4.json5;

import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;

/**
 *
 * @author noear 2025/10/9 created
 *
 */
public class ReadTest {
    @Test
    public void case1() { //对象字面量增强
        String json = "{\n" +
                "    // 允许尾随逗号\n" +
                "    key1: \"value\",\n" +
                "    key2: \"value\",  // 这个逗号不会报错\n" +
                "    \n" +
                "    // 允许未加引号的键名（符合有效的 JavaScript 标识符规则）\n" +
                "    unquotedKey: \"value\",\n" +
                "    _anotherKey: \"value\",\n" +
                "    $specialKey: \"value\"\n" +
                "}";

        ONode.ofJson(json, Feature.Read_AllowComment);
    }

    @Test
    public void case2() { //字符串字面量扩展
        String json = "{\n" +
                "    // 支持单引号\n" +
                "    singleQuotes: 'hello world',\n" +
                "    \n" +
                "    // 支持多行字符串\n" +
                "    multiline: \"这是第一行\n" +
                "    这是第二行\n" +
                "    这是第三行\",\n" +
                "    \n" +
                "    // 增强的转义字符\n" +
                "    escapes: \"\\x21 \\u0021 \\n \\t\", // 支持所有 JavaScript 转义序列\n" +
                "}";

        ONode.ofJson(json, Feature.Read_AllowComment);
    }


    @Test
    public void case3() { //数字字面量增强
        String json = "{\n" +
                "    // 支持十六进制\n" +
                "    hex: 0xDECAF,\n" +
                "    \n" +
                "    // 支持显式的正号\n" +
                "    positive: +42,\n" +
                "    \n" +
                "    // 支持浮点数的前导和小数点后小数点\n" +
                "    leadingDecimal: .5,\n" +
                "    trailingDecimal: 5.,\n" +
                "    \n" +
                "    // 支持 Infinity 和 NaN\n" +
                "    infinity: Infinity,\n" +
                "    negativeInfinity: -Infinity,\n" +
                "    notANumber: NaN,\n" +
                "    \n" +
                "    // 改进的数字可读性\n" +
                "    readable: 1_000_000,  // 数字分隔符\n" +
                "    scientific: 1.23e+45\n" +
                "}";

        ONode.ofJson(json, Feature.Read_AllowComment);
    }


    @Test
    public void case4() { //注释支持
        String json = "{\n" +
                "    // 这是单行注释\n" +
                "    \"key\": \"value\",\n" +
                "    \n" +
                "    /*\n" +
                "     * 这是多行注释\n" +
                "     * 可以跨越多行\n" +
                "     */\n" +
                "    \"anotherKey\": \"value\"\n" +
                "}";

        ONode.ofJson(json, Feature.Read_AllowComment);
    }

    @Test
    public void case5() {
        String json = "";
        ONode.ofJson(json);
    }

}
