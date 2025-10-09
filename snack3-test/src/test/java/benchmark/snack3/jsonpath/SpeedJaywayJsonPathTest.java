package benchmark.snack3.jsonpath;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONValue;
import org.junit.jupiter.api.Test;


public class SpeedJaywayJsonPathTest {


    @Test
    public void test1(){
        //1000000=>2658,2633,2590 //2916,3303,2957
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        ReadContext context = JsonPath.parse(text);

        JSONArray rst = context.read("$..a");
        System.out.println(rst);
        assert rst.size() ==2;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            context.read("$..a");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test2(){
        //1000000=>3227,3220,3156 //4112,4516,4292
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        ReadContext context = JsonPath.parse(text);

        JSONArray rst = context.read("$..*");
        System.out.println(rst);
        assert rst.size() == 16;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            context.read("$..*");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test3(){
        //1000000=>782,798,776 //796,772,783
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        ReadContext context = JsonPath.parse(text);

        JSONArray rst = context.read("$.data.list[1,4]");
        System.out.println(rst);
        assert rst.size() == 2;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            context.read("$.data.list[1,4]");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test4(){
        //1000000=>941,899,947 //880,958,954
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        ReadContext context = JsonPath.parse(text);

        JSONArray rst = context.read("$.data.list[1:4]");
        System.out.println(rst);
        assert rst.size() == 3;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            context.read("$.data.list[1:4]");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test40(){
        //1000000=>704,663,655 //730,722,789
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        ReadContext context = JsonPath.parse(text);

        Object rst = context.read("$.data.ary2[0].a");
        System.out.println(rst);
        assert rst instanceof Integer;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            context.read("$.data.ary2[0].a");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test41(){
        //1000000=>2522,2551,2591 //2468,2546,2508
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        ReadContext context = JsonPath.parse(text);

        JSONArray rst = context.read("$..ary2[0].a");
        System.out.println(rst);
        assert rst.size() == 1;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            context.read("$..ary2[0].a");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test42() {
        //1000000=>5494,5326,5483 //5803,5936,6038
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        ReadContext context = JsonPath.parse(text);

//        Object tmp00 = JsonPath.read(obj,"$..ary2");
//        Object tmp01 = JsonPath.read(obj,"$..list[2]");
//        Object tmp0 = JsonPath.read(obj,"$..list[-2:]");
//        Object tmp1 = JsonPath.read(obj,"$..ary2[0].a");
//
//        Object tmp2 = JsonPath.read(obj,"$.data.list[?(@ == $..ary2[0].a.min())]");

        JSONArray tmp30 = context.read("$..ary2[0].a");
        assert tmp30.size() == 1;

        JSONArray tmp3 = context.read("$.data.list[?(@ in $..ary2[0].a)]");
        assert tmp3.size() == 1;


        long start = System.currentTimeMillis();
        for (int i = 0, len = 1000000; i < len; i++) {
            context.read("$.data.list[?(@ == $..ary2[0].a)]");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test5(){
        //1000000=>929,826,837 //809,852,759
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        ReadContext context = JsonPath.parse(text);

        Object tmp = context.read("data.ary2[1].b.c");;
        assert "ddd".equals(tmp);

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            context.read("$.data.ary2[1].b.c");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test6(){
        //1000000=>1105,1025,1050 //1014,1061,1069
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        ReadContext context = JsonPath.parse(text);

        JSONArray tmp = context.read("$.data.ary2[*].b.c");
        assert tmp.size() == 1;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            context.read("$.data.ary2[*].b.c");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test7(){
        //1000000=>5628,5739,5636 //3305,3471,3360
        //
        //1.加载json
        String text = ("[{b:{c:1}}, {b:{d:1}}, {b:{c:2}}, {b:{c:23}}]");
        ReadContext context = JsonPath.parse(text);

        JSONArray tmp = context.read("$..b[?(@.c == 12)]");
        assert tmp.size() == 0;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            context.read("$..b[?(@.c == 12)]");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test8(){
        //....      //3278,3324,3250
        //运行会出错
        //1.加载json
        String text =("[{b:{c:1}}, {b:{d:1}}, {b:{c:2}}, {b:{c:23}}]");
        ReadContext context = JsonPath.parse(text);

        JSONArray tmp = context.read("$..c");
        assert tmp.size() == 3;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            context.read("$..c.min()");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test9(){
        //1000000=>3575,3591,3813  //1918,1927,1854
        //
        //1.加载json
        String text = ("[{c:'aaaa'}, {b:'cccc'}, {c:'cccaa'}]");
        ReadContext context = JsonPath.parse(text);

        JSONArray tmp = context.read("$[?(@.c =~ /.*a+/)]");
        assert tmp.size() == 2;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            context.read("$[?(@.c =~ /a+/)]");//
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }
}
