package features.snack4.json;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 *
 * @author noear 2025/10/20 created
 *
 */
public class ParseTest {
    @Test
    public void case1() {
        String json = "{\"id\":\"3a000b57-04d8-4e61-adba-5e0f871389c9\",\"object\":\"chat.completion.chunk\",\"created\":1760932388,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_ffc7281d48_prod0820_fp8_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"tool_calls\":[{\"index\":0,\"id\":\"call_00_NavXIvoHFNnJUYFCvU2B1bjO\",\"type\":\"function\",\"function\":{\"name\":\"get_weather\",\"arguments\":\"\"}}]},\"logprobs\":null,\"finish_reason\":null}]}";
        ONode.ofJson(json);
    }
}
