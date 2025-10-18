package benchmark.snack4.jsonpath;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;


public class SpeedFastjson2JsonPathTest {


    @Test
    public void test1(){
        //1000000=>872,764,715
        //
        //1.加载json
        String text = ONode.ofJson("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}").toJson();
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$..a");
        System.out.println(tmp);
        assert tmp instanceof JSONArray;
        assert ((JSONArray)tmp).size() ==2;

        JSONPath jsonPath = JSONPath.of("$..a");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            jsonPath.eval(obj);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test2(){
        //1000000=>1269,1272,1257  //1070,1060,1098
        //
        //1.加载json
        String text = ONode.ofJson("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}").toJson();
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$..*");
        System.out.println(tmp);
        assert tmp instanceof JSONArray;
        assert ((JSONArray)tmp).size() ==16;

        JSONPath jsonPath = JSONPath.of("$..*");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            jsonPath.eval(obj);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test3(){
        //1000000=>577,524,419  //218,209,205
        //
        //1.加载json
        String text = ONode.ofJson("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}").toJson();
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$.data.list[1,4]");
        System.out.println(tmp);
        assert tmp instanceof JSONArray;
        assert ((JSONArray)tmp).size() ==2;

        JSONPath jsonPath = JSONPath.of("$.data.list[1,4]");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            jsonPath.eval(obj);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test4(){
        //1000000=>332,367,391  //163,185,185
        //
        //1.加载json
        String text = ONode.ofJson("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}").toJson();
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$.data.list[1:4]");
        System.out.println(tmp);
        assert tmp instanceof JSONArray;
        assert ((JSONArray)tmp).size() ==3;

        JSONPath jsonPath = JSONPath.of("$.data.list[1:4]");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            jsonPath.eval(obj);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test40(){
        //1000000=>315,339,329  //150,147,154
        //
        //1.加载json
        String text = ONode.ofJson("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}").toJson();
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$.data.ary2[0].a");
        System.out.println(tmp);
        assert tmp instanceof Integer;

        JSONPath jsonPath = JSONPath.of("$.data.ary2[0].a");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            jsonPath.eval(obj);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test41(){
        //1000000=>735,728,736 //841,823,834
        //
        //1.加载json
        String text = ONode.ofJson("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}").toJson();
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$..ary2[0].a");
        System.out.println(tmp);
        assert tmp instanceof Integer;

        JSONPath jsonPath = JSONPath.of("$..ary2[0].a");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            jsonPath.eval(obj);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test42(){
        //出错
        //
        //1.加载json
        String text = ONode.ofJson("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}").toJson();
        JSONObject obj = JSON.parseObject(text);

        JSONPath.eval(obj,"$.data.list[?(@ in $..ary2[0].a)]");


        JSONPath jsonPath = JSONPath.of("$.data.list[?(@ in $..ary2[0].a)]");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            jsonPath.eval(obj);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test5(){
        //1000000=>422,424,415 //165,182,165
        //
        //1.加载json
        String text = ONode.ofJson("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}").toJson();
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$.data.ary2[1].b.c");
        System.out.println(tmp);
        assert tmp instanceof String;

        JSONPath jsonPath = JSONPath.of("$.data.ary2[1].b.c");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            jsonPath.eval(obj);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test6(){
        //1000000=>642,645,660  //452,467,424
        //
        //1.加载json
        String text = ONode.ofJson("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}").toJson();
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$.data.ary2[*].b.c");
        System.out.println(tmp);
        assert tmp instanceof JSONArray;
        assert ((JSONArray)tmp).size() ==1;

        JSONPath jsonPath = JSONPath.of("$.data.ary2[*].b.c");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            jsonPath.eval(obj); //不支持*
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test7(){
        //1000000=>605,587,615
        //
        //1.加载json
        String text = ONode.ofJson("[{b:{c:1}}, {b:{d:1}}, {b:{c:2}}, {b:{c:23}}]").toJson(); //解析会出错
        JSONArray obj = JSON.parseArray(text);

        Object tmp = JSONPath.eval(obj,"$..b[?(@.c == 12)]");//不支持
        assert tmp instanceof JSONArray;
        assert ((JSONArray)tmp).size() == 0;

        JSONPath jsonPath = JSONPath.of("$..b[?(@.c == 12)]");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            jsonPath.eval(obj);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test8(){
        //不支持
        //
        //1.加载json
        String text = ONode.ofJson("[{b:{c:1}}, {b:{d:1}}, {b:{c:2}}, {b:{c:23}}]").toJson();//解析会出错
        JSONArray obj = JSON.parseArray(text);

        JSONPath.eval(obj,"$..c.min()");

        JSONPath jsonPath = JSONPath.of("$..c.min()");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            jsonPath.eval(obj);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test9(){
        //1000000=>288,290,284
        //
        //1.加载json
        String text = ONode.ofJson("[{c:'aaaa'}, {b:'cccc'}, {c:'cccaa'}]").toJson();
        JSONArray obj = JSON.parseArray(text);

        Object tmp = JSONPath.eval(obj,"$[?(@.c =~ /a+/)]");//不支持
        System.out.println(tmp);
        assert ((JSONArray)tmp).size() == 1;

        JSONPath jsonPath = JSONPath.of("$[?(@.c =~ /a+/)]");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            jsonPath.eval(obj);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }
}
