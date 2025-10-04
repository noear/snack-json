package labs;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;

/**
 * @author noear 2025/3/17 created
 */
public class DemoLab {
    public void case1() {
        ONode oNode = new ONode();
        oNode.set("id", 1);
        oNode.getOrNew("layout").then(o -> {
            o.addNew().set("title", "开始").set("type", "start");
            o.addNew().set("title", "结束").set("type", "end");
        });
    }

    public void case2() {
        String store = "{}";
        ONode.from(store).select("$..book[?(@.tags contains 'war')]").toBean(Book.class);
        ONode.load(store).select("$.store.book.count()");

        ONode.from(store).create("$.store.book[0].category").toJson();

        ONode.from(store).delete("$..book[-1]");
    }

    @Test
    public void case4() {
        String jsonpath = "$.simpleReportList[?(@.reportStyle == \"1\")].rows[?(@.projectCode == \"BS010101\")].columns[?(@.columnCode == \"#YEAR#YTD#Period#Actual#ACCOUNT#[ICP None]#[None]#BB#REPORT#PRCTotal\")].value";

        ONode obj = new ONode();
        obj.create(jsonpath);

        System.out.println(obj.toJson());
    }

    static class Book {
    }
}
