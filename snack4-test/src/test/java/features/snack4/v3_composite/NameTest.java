package features.snack4.v3_composite;

import demo.snack4._models.BookModel;
import demo.snack4._models.BookViewModel;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.core.Feature;
import org.noear.snack4.core.Options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2021/1/1 created
 */
public class NameTest {
    @Test
    public void test() {
        BookModel user = new BookModel();
        user.id = 12;
        user.bookname = "noear";
        user.note = "test";

        String json = ONode.ofBean(user).toJson();
        System.out.println(json);

        assert json.contains("name");


        BookModel user2 = ONode.ofJson(json).toBean(BookModel.class);
        System.out.println(user2.bookname);

        assert "noear".equals(user2.bookname);
    }


    @Test
    public void test2() {
        BookViewModel vm = new BookViewModel();
        vm.list = new ArrayList<>();

        BookModel user = new BookModel();
        user.id = 12;
        user.bookname = "noear";
        user.note = "test";

        vm.list.add(user);


        user = new BookModel();
        user.id = 13;
        user.bookname = "ddd";
        user.note = "test";

        vm.list.add(user);

        String json = ONode.ofBean(vm).toJson();
        System.out.println(json);

        assert json.contains("name");


        BookViewModel vm2 = ONode.ofJson(json).toBean(BookViewModel.class);
        System.out.println(vm2.list.get(0).bookname);

        assert "noear".equals(vm2.list.get(0).bookname);
    }


    @Test
    public void test4() {
        Map<String, Object> data = new HashMap<>();
        data.put("c:\\", "c:\\");

        String json = ONode.ofBean(data, options).toJson();
        System.out.println(json);

        String json2 = ONode.ofJson(json).toJson();
        System.out.println(json2);
        assert json2.equals(json);
    }

    private static final Options options = Options.of(Feature.Write_EnumUsingName);
}