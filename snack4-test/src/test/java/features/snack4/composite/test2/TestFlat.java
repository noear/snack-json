package features.snack4.composite.test2;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.annotation.ONodeAttr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author bknife
 * @Date 2024/10/22
 * @Description 扁平化测试
 */
public class TestFlat {

    @Data
    public static class TestFlatChildDto {
        private Integer age;
        private Boolean result;
    }

    @Data
    public static class TestFlatChildDto2 {
        private Integer age2;
        private Boolean result2;
    }

    @Data
    public static class TestFlatDto {
        private String name;
        @ONodeAttr(flatten = true)
        private TestFlatChildDto child;
        @ONodeAttr(flatten = true)
        private final TestFlatChildDto2 child2 = new TestFlatChildDto2();

    }

    @Test
    public void test01() {
        String json = "{\"name\":\"test\",\"age\":34,\"result\":true,\"age2\":54,\"result2\":false}";

        TestFlatDto dto = ONode.load(json).to(TestFlatDto.class);

        assertEquals(dto.getName(), "test");

        TestFlatChildDto childDto = dto.getChild();
        assertNotNull(childDto);
        assertEquals(childDto.getAge(), 34);
        assertEquals(childDto.getResult(), true);

        TestFlatChildDto2 childDto2 = dto.getChild2();
        assertNotNull(childDto2);
        assertEquals(childDto2.getAge2(), 54);
        assertEquals(childDto2.getResult2(), false);

        String str = ONode.from(dto).toJson();
        assertEquals(str, json);
    }
}
