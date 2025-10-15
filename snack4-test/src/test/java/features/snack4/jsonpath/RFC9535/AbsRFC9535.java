package features.snack4.jsonpath.RFC9535;

import org.noear.snack4.core.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.core.Options;

/**
 *
 * @author noear 2025/10/11 created
 *
 */
public class AbsRFC9535 {
    final Options options = Options.of(Feature.JsonPath_SuppressExceptions);

    protected ONode ofJson(String json) {
        return ONode.ofJson(json, options);
    }

    protected ONode queryOf(String json, String query) {
        return ofJson(json).select(query);
    }
}