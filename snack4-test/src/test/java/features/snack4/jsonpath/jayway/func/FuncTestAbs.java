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
        Number tmp2 = null;
        Throwable err1 = null;
        Throwable err2 = null;

        try {
            ONode oNode = ONode.ofJson(JSON_DATA(), Feature.JsonPath_JaywayMode);
            tmp = oNode.select(jsonpathStr);
            System.out.println(tmp.toJson());
        } catch (Exception e) {
            err1 = e;
            System.err.println(e.getMessage());
        }

        try {
            DocumentContext context = JsonPath.parse(JSON_DATA());
            tmp2 = context.read(jsonpathStr);
            System.out.println(ONode.ofBean(tmp2).toJson());
        } catch (Exception e) {
            err2 = e;
            System.err.println(e.getMessage());
        }

        if (err1 != null && err2 != null) {
            return;
        }

        if(tmp.isNull() && tmp2 == null) {
            return;
        }

        assert Math.abs(tmp.getDouble() - tmp2.doubleValue()) < 0.001;

//        if (tmp2 != null) {
//            assert Math.abs(tmp.getDouble() - tmp2) < 0.001;
//        } else {
//            assert Math.abs(tmp.getDouble() - Double.parseDouble(ref)) < 0.001;
//        }
    }

    protected void compatible_str(String tag, String ref, String jsonpathStr) {
        System.out.println("::::" + tag + " - " + jsonpathStr);

        ONode tmp = null;
        Object tmp2 = null;
        Throwable err1 = null;
        Throwable err2 = null;

        try {
            ONode oNode = ONode.ofJson(JSON_DATA(), Options.of().addFeatures(Feature.JsonPath_JaywayMode));
            tmp = oNode.select(jsonpathStr);
            System.out.println(tmp.toJson());
        } catch (Exception e) {
            err1 = e;
            System.err.println(e.getMessage());
        }

        try {
            DocumentContext context = JsonPath.parse(JSON_DATA());
            tmp2 = context.read(jsonpathStr);
            System.out.println(ONode.ofBean(tmp2).toJson());
        } catch (Exception e) {
            err2 = e;
            System.err.println(e.getMessage());
        }

        if (err1 != null && err2 != null) {
            return;
        }

        if (tmp.toJson().equals(ONode.ofBean(tmp2).toJson()) == false) {
            if (tmp.toJson().equals(ref) == false) {
                System.out.println(JSON_DATA());
                assert false;
            }
        }
    }
}
