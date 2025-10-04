package features.schema;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.exception.SchemaException;
import org.noear.snack4.json.JsonReader;
import org.noear.snack4.jsonschema.JsonSchemaValidator;

import java.io.IOException;
import java.io.StringReader;

/**
 * @author noear 2025/5/10 created
 */
public class JsonSchemaTest {
    @Test
    public void case1() {
        JsonSchemaValidator schema = new JsonSchemaValidator(ONode.load("{type:'object',properties:{userId:{type:'string'}}}")); //加载架构定义

        schema.validate(ONode.load("{userId:'1'}")); //校验格式
    }

    @Test
    public void case2() {
        JsonSchemaValidator schema = new JsonSchemaValidator(ONode.load("{type:'object',properties:{userId:{type:'string'}}}")); //加载架构定义

        Throwable err = null;
        try {
            schema.validate(ONode.load("{userId:1}"));//校验格式
        } catch (Throwable e) {
            e.printStackTrace();
            err = e;
        }

        assert err != null;
    }


    @Test
    public void case3() throws IOException {
        // Schema定义示例
        String schemaJson = "{"
                + "\"type\": \"object\","
                + "\"required\": [\"name\", \"age\"],"
                + "\"properties\": {"
                + "  \"name\": {\"type\": \"string\"},"
                + "  \"age\": {\"type\": \"integer\", \"minimum\": 0}"
                + "}"
                + "}";


        System.out.println(schemaJson);

        // 数据校验
        JsonReader parser = new JsonReader(new StringReader(schemaJson));
        ONode schemaNode = parser.read();
        JsonSchemaValidator validator = new JsonSchemaValidator(schemaNode);

        ONode data = new JsonReader(new StringReader("{\"name\":\"Alice\",\"age\":-5}")).read();
        try {
            validator.validate(data);
        } catch (SchemaException e) {
            System.out.println(e.getMessage());
            // 输出: Value -5.0 < minimum(0.0) at $.age
        }
    }
}