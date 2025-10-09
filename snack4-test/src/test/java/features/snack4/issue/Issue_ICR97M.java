package features.snack4.issue;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 *
 * @author noear 2025/10/9 created
 *
 */
public class Issue_ICR97M {
    @Test
    public void case1() {
        String jsonpath = "$.simpleReportList[?(@.reportStyle == \"1\")].rows[?(@.projectCode == \"BS010101\")].columns[?(@.columnCode == \"#YEAR#YTD#Period#Actual#ACCOUNT#[ICP None]#[None]#BB#REPORT#PRCTotal\")].value";

        ONode result = ONode.ofJson("{}").create(jsonpath);

        System.out.println(result);
        assert result != null;
        assert result.isObject();
        assert result.get("simpleReportList").isObject();
        assert result.get("simpleReportList").get(0).get("rows").isArray();
        assert result.get("simpleReportList").get(0).get("rows").get(0).get("columns").isObject();
    }
}
