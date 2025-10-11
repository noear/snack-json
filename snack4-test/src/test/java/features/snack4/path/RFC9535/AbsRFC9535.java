package features.snack4.path.RFC9535;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.Standard;

/**
 *
 * @author noear 2025/10/11 created
 *
 */
public class AbsRFC9535 {
    final Options options = Options.of().addStandard(Standard.JSONPath_IETF_RFC_9535);

    protected ONode ofJson(String json) {
        return ONode.ofJson(json, options);
    }
}
