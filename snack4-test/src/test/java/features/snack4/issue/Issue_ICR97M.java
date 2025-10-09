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

        ONode oNode = new ONode();
        oNode.create(jsonpath).then(e -> e.get(0).setValue("1"));

        System.out.println(oNode.toJson());

        assert oNode != null;
        assert oNode.isObject();
        assert oNode.get("simpleReportList").isArray();
        assert oNode.get("simpleReportList").get(0).get("rows").isArray();
        assert oNode.get("simpleReportList").get(0).get("rows").get(0).get("columns").isArray();

        assert "{\"simpleReportList\":[{\"reportStyle\":\"1\",\"rows\":[{\"projectCode\":\"BS010101\",\"columns\":[{\"columnCode\":\"#YEAR#YTD#Period#Actual#ACCOUNT#[ICP None]#[None]#BB#REPORT#PRCTotal\",\"value\":\"1\"}]}]}]}".equals(oNode.toJson());
    }
}
