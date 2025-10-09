package features.snack4.issue;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 *
 * @author noear 2025/10/9 created
 *
 */
public class Issue_ICRNNQ {
    @Test
    public void case1() {
        String json = " {\n" +
                "  \"projectCode\" : \"IS0101\",\n" +
                "  \"columns\" : [ {\n" +
                "    \"columnCode\" : \"#YEAR(-1)#YTD#Period#Actual#ACCOUNT#[ICP None]#[None]#[None]#REPORT#PRCTotal\",\n" +
                "    \"value\" : \"1\"\n" +
                "  } ]\n" +
                "}";

        ONode result = ONode.ofJson(json).select("$.columns[?(@.columnCode == '#YEAR(-1)#YTD#Period#Actual#ACCOUNT#[ICP None]#[None]#[None]#REPORT#PRCTotal')].first()");

        System.out.println(result);
        assert result != null;
        assert result.isObject();
    }
}
