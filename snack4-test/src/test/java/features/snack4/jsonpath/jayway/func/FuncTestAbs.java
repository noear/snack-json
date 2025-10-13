package features.snack4.jsonpath.jayway.func;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;

/**
 *
 * @author noear 2025/10/12 created
 *
 */
public abstract class FuncTestAbs {
    protected abstract String JSON_DATA();

    protected void compatible_num(String tag, String ref, String jsonpathStr) {
        System.out.println("::::" + tag + " - " + jsonpathStr);

        ONode tmp = null;
        Double tmp2 = null;
        Throwable err1 = null;
        Throwable err2 = null;

        try {
            ONode oNode = ONode.ofJson(JSON_DATA(), Options.of().addFeature(Feature.JsonPath_JaywayMode));
            tmp = oNode.select(jsonpathStr);
            System.out.println(tmp.toJson());
        } catch (Exception e) {
            err1 = e;
            System.err.println(e.getMessage());
        }

        try {
            DocumentContext context = JsonPath.parse(JSON_DATA());
            tmp2 = context.read(jsonpathStr);
            System.out.println(ONode.serialize(tmp2));
        } catch (Exception e) {
            err2 = e;
            System.err.println(e.getMessage());
        }

        if (err1 != null && err2 != null) {
            return;
        }

        assert Math.abs(tmp.getDouble() - tmp2) < 0.001;
        ;
    }

    protected void compatible_str(String tag, String ref, String jsonpathStr) {
        System.out.println("::::" + tag + " - " + jsonpathStr);

        ONode tmp = null;
        Object tmp2 = null;
        Throwable err1 = null;
        Throwable err2 = null;

        try {
            ONode oNode = ONode.ofJson(JSON_DATA(), Options.of().addFeature(Feature.JsonPath_JaywayMode));
            tmp = oNode.select(jsonpathStr);
            System.out.println(tmp.toJson());
        } catch (Exception e) {
            err1 = e;
            System.err.println(e.getMessage());
        }

        try {
            DocumentContext context = JsonPath.parse(JSON_DATA());
            tmp2 = context.read(jsonpathStr);
            System.out.println(ONode.serialize(tmp2));
        } catch (Exception e) {
            err2 = e;
            System.err.println(e.getMessage());
        }

        if (err1 != null && err2 != null) {
            return;
        }

        assert tmp.toJson().equals(ONode.serialize(tmp2));
    }
}
