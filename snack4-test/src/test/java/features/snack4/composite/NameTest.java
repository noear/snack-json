package features.snack4.composite;

import demo.snack4._models.BookModel;
import demo.snack4._models.BookViewModel;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Feature;
import org.noear.snack4.Options;

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

        String json = ONode.toJson(user);
        System.out.println(json);

        assert json.contains("name");


        BookModel user2 = ONode.fromJson(json, BookModel.class);
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

        String json = ONode.toJson(vm);
        System.out.println(json);

        assert json.contains("name");


        BookViewModel vm2 = ONode.fromJson(json, BookViewModel.class);
        System.out.println(vm2.list.get(0).bookname);

        assert "noear".equals(vm2.list.get(0).bookname);
    }

    @Test
    public void test3() {
        Options options = Options.enableOf(
                Feature.Write_QuoteFieldNames);

        String val = new ONode(options).get("name").getString();
        System.out.println(val);
        assert val == null;


        String val2 = new ONode().get("name").getString();
        System.out.println(val2);
        assert val2 == null;
    }

    @Test
    public void test4() {
        Map<String, Object> data = new HashMap<>();
        data.put("c:\\", "c:\\");

        String json = ONode.toJson(data, options);
        System.out.println(json);

        String json2 = ONode.fromJson(json).toJson();
        System.out.println(json2);
        assert json2.equals(json);
    }

    private static final Options options = Options.enableOf(Feature.Write_EnumUsingName);
}