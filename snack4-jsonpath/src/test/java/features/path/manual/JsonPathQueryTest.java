package features.path.manual;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author noear 2025/5/5 created
 */
public class JsonPathQueryTest {
    @Test
    public void getAllTest() throws Exception {
        ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");

        ONode tmp = n.select("$..*");
        System.out.println(tmp);//打印
        assertEquals(16, tmp.size());
    }

    @Test
    public void getAllPropTest() throws Exception {
        ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");

        ONode tmp = n.select("$..a");
        System.out.println(tmp);//打印
        assertEquals(2, tmp.size());
    }

    @Test
    public void getArrayMoreIndexTest() throws Exception {
        ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");

        ONode tmp = n.select("$.data.list[1,4]");
        System.out.println(tmp);//打印
        assertEquals(2, tmp.size());
    }

    @Test
    public void getArrayRangeTest() {
        ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");

        ONode tmp = n.select("$.data.list[1:4]");
        System.out.println(tmp);//打印
        assertEquals(3, tmp.size());
    }

    @Test
    public void getFilterAndRightExpressionTest() throws Exception {
        ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");

        ONode tmp = n.select("$.data.list[?(@ in $..ary2[0].a)]");
        assert tmp.size() == 1;
        assertEquals(1, tmp.size());
    }

    @Test
    public void getAllAndPropTest() throws Exception {
        String json = "{\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95},{\"category\":\"fiction\",\"author\":\"Evelyn Waugh\",\"title\":\"Sword of Honour\",\"price\":12.99,\"isbn\":\"0-553-21311-3\"}],\"bicycle\":{\"color\":\"red\",\"price\":19.95}}}";
        ONode n = ONode.load(json);

        ONode t7 = n.select("$..book[*].author");
        System.out.println(t7);
        assert t7.isArray() && t7.size() == 2;

        ONode t8 = n.select("$..book.author");
        System.out.println(t8);
        assert t8.isArray() && t8.size() == 0;
    }
}