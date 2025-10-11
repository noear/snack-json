package features.snack4.path.RFC9535;

import org.junit.jupiter.api.Test;

/**
 *
 * @author noear 2025/10/11 created
 *
 */
public class RFC9535_s2490_Function_no extends AbsRFC9535{
    // SQL/JSON Path (ISO/IEC 9075)
    // IETF JSONPath (RFC 9535) https://www.rfc-editor.org/rfc/rfc9535.html

    @Test
    public void case1() {
        queryAssert("$[?length(@) < 3]", "xxx"); //类型良好
        queryAssert("$[?length(@.*) < 3]", "xxx"); //@.*由于是非单数查询，因此类型不正确
        queryAssert("$[?count(@.*) == 1]", "xxx"); //类型良好
        queryAssert("$[?count(1) == 1]", "xxx"); //1由于不是查询或函数表达式，因此类型不正确
        queryAssert("$[?count(foo(@.*)) == 1]", "xxx"); //类型良好，其中是具有类型参数和结果类型foo()的函数扩展NodesTypeNodesType
        queryAssert("$[?match(@.timezone, 'Europe/.*')]", "xxx"); //类型良好
        queryAssert("$[?match(@.timezone, 'Europe/.*') == true]", "xxx"); //类型不正确，因为LogicalType可能无法用于比较
        queryAssert("$[?value(@..color) == \"red\"]", "xxx"); //类型良好
        queryAssert("$[?value(@..color)]", "xxx"); //类型不正确，因为ValueType可能无法在测试表达式中使用
        queryAssert("$[?bar(@.a)]", "xxx"); //bar()对于具有任何声明类型和结果类型的参数的任何函数都是类型良好的LogicalType
        queryAssert("$[?bnl(@.*)]", "xxx"); //对于具有声明类型或结果类型bnl()的参数的任何函数而言，类型良好NodesTypeLogicalTypeLogicalType
        queryAssert("$[?blt(1==1)]", "xxx"); //类型良好，其中是具有声明类型和结果类型blt()的参数的函数LogicalTypeLogicalType
        queryAssert("$[?blt(1)]", "xxx"); //对于相同的函数来说类型不正确blt()，因为1不是查询logical-expr、或函数表达式
        queryAssert("$[?bal(1)]", "xxx"); //类型良好，其中是具有声明类型和结果类型bal()的参数的函数ValueTypeLogicalType
    }

    private void queryAssert(String expr, String expected) {

    }
}
