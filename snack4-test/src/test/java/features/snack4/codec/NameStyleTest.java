package features.snack4.codec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.json.util.NameUtil;

/**
 *
 * @author noear 2025/10/30 created
 *
 */
public class NameStyleTest {
    @Test
    public void case1() {
        StringBuilder buf = new StringBuilder();

        Assertions.assertEquals("userName", NameUtil.toSmlCamelStyle(buf, "user_name"));
        Assertions.assertEquals("userName", NameUtil.toSmlCamelStyle(buf, "userName"));
        Assertions.assertEquals("firstName", NameUtil.toSmlCamelStyle(buf, "first_name"));
        Assertions.assertEquals("firstName", NameUtil.toSmlCamelStyle(buf, "firstName"));

        Assertions.assertEquals("lastName", NameUtil.toSmlCamelStyle(buf, "last_name"));
        Assertions.assertEquals("emailAddress", NameUtil.toSmlCamelStyle(buf, "email_address"));
        Assertions.assertEquals("isActive", NameUtil.toSmlCamelStyle(buf, "is_active"));
        Assertions.assertEquals("createdAt", NameUtil.toSmlCamelStyle(buf, "created_at"));
        Assertions.assertEquals("user", NameUtil.toSmlCamelStyle(buf, "user"));
        Assertions.assertEquals("", NameUtil.toSmlCamelStyle(buf, ""));
        Assertions.assertEquals("userName", NameUtil.toSmlCamelStyle(buf, "user__name"));
    }

    @Test
    public void case2() {
        StringBuilder buf = new StringBuilder();

        Assertions.assertEquals("user_name", NameUtil.toSmlSnakeStyle(buf, "user_name"));
        Assertions.assertEquals("user_name", NameUtil.toSmlSnakeStyle(buf, "userName"));
        Assertions.assertEquals("first_name", NameUtil.toSmlSnakeStyle(buf, "first_name"));
        Assertions.assertEquals("first_name", NameUtil.toSmlSnakeStyle(buf, "firstName"));

        Assertions.assertEquals("last_name", NameUtil.toSmlSnakeStyle(buf, "lastName"));
        Assertions.assertEquals("email_address", NameUtil.toSmlSnakeStyle(buf, "emailAddress"));
        Assertions.assertEquals("is_active", NameUtil.toSmlSnakeStyle(buf, "isActive"));
        Assertions.assertEquals("created_at", NameUtil.toSmlSnakeStyle(buf, "createdAt"));
        Assertions.assertEquals("user", NameUtil.toSmlSnakeStyle(buf, "user"));
        Assertions.assertEquals("", NameUtil.toSmlSnakeStyle(buf, ""));
        Assertions.assertEquals("user_name", NameUtil.toSmlSnakeStyle(buf, "userName"));
    }
}