package features.snack4.composite;

import demo.snack4._model4.QueryParamEntity;
import demo.snack4._models.*;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Feature;
import org.noear.snack4.Options;
import org.noear.snack4.codec.TypeRef;
import org.noear.solon.Solon;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.*;

public class SerializationsTest2 {

    public Object buildObj() {
        UserGroupModel group = new UserGroupModel();
        group.id = 9999;
        group.users = new ArrayList<>();
        group.users2 = new LinkedHashMap<>();
        group.users3 = new TreeSet<>();
        group.names = new String[5];
        group.ids = new short[5];
        group.iids = new Integer[5];
        group.dd = new BigDecimal(12);
        group.tt1 = new Timestamp(new Date().getTime());
        group.tt2 = new Date();

        for (short i = 0; i < 5; i++) {
            UserModel user = new UserModel();
            user.id = i;
            user.name = "张三" + i;
            user.note = null;
            group.users.add(user);
            group.users2.put(Integer.valueOf(i), user);
            group.names[i] = "李四" + i;
            group.ids[i] = i;
        }

        return group;
    }

    public String buildJson() {
        return ONode.from(buildObj()).serialize();
    }

    @Test
    public void test01() {
        String tmp = ONode.serialize(buildObj(), Feature.Write_ClassName);
        System.out.println(tmp);
    }

    @Test
    public void test02() {
        String tmp = ONode.serialize(buildObj(), Feature.Write_ClassName);
        tmp = tmp.replaceAll("UserGroupModel", "UserGroupModel2");
        UserGroupModel2 tmp2 = ONode.deserialize(tmp, UserGroupModel2.class);

        assert tmp2.users != null;
        assert tmp2.users.length > 2;
        System.out.println(tmp2);
    }

    @Test
    public void test10() throws Exception {
        String json0 = buildJson();

        System.out.println(json0);
        UserGroupModel group0 = ONode.load(json0)
                .to((new TypeRef<UserGroupModel>() {
                }));

        assert group0.id == 9999;
    }

    @Test
    public void test11() throws Exception {
        String json0 = buildJson();

        System.out.println(json0);
        UserGroupModel group0 = ONode.load(json0)
                .to(UserGroupModel.class);

        assert group0.id == 9999;
    }

    @Test
    public void test20() throws Exception {
        String json0 = buildJson();

        System.out.println(json0);
        List<UserModel> group0 = ONode.load(json0).get("users")
                .to((new ArrayList<UserModel>() {
                }).getClass());

        assert group0.size() == 5;
    }

    @Test
    public void test21() throws Exception {
        String json0 = buildJson();

        System.out.println(json0);
        List<UserModel> group0 = ONode.load(json0).get("users")
                .to((new TypeRef<List<UserModel>>() {
                }).getClass());

        assert group0.size() == 5;
    }

    @Test
    public void test3() {
        String queryString = "pageIndex=0&pageSize=10&sorts[0].name=time&sorts[0].order=desc&terms[0].column=source&terms[0].value=SciVault&terms[1].column=descriptionFilter$LIKE&terms[1].value=%25aaa%25&terms[2].column=time$btw&terms[2].value=1660492800000,1661184000000&excludes=return_filters";
        String[] kvAry = queryString.split("&");
        Properties props = new Properties();

        for (String kvStr : kvAry) {
            String[] kv = kvStr.split("=");
            props.setProperty(kv[0], kv[1]);
        }

        ONode oNode = ONode.from(props);

        System.out.println(oNode.serialize());

        QueryParamEntity entity = oNode.to(QueryParamEntity.class);

        assert entity != null;
        assert entity.getPageIndex() == 0;
        assert entity.getPageSize() == 10;
        assert entity.getSorts().size() > 0;
//        assert entity.getTerms().size() > 0;
//        assert entity.getExcludes().size() == 1;
    }

    @Test
    public void test4() {
        Properties properties = Solon.cfg().getProp("test1");
        PersonColl tmp = ONode.from(properties).to(PersonColl.class);

        assert tmp != null;
        assert tmp.getUsers() != null;
        assert tmp.getUsers().size() == 2;
        assert tmp.getUsers().get("user1") instanceof Person;
    }

    @Test
    public void test5() {
        String json = "{data:{a:1,b:2}}";
        MapModel mapModel = ONode.deserialize(json, MapModel.class);

        assert mapModel != null;
        assert mapModel.data != null;
        assert mapModel.data.size() == 2;
    }

    @Test
    public void test6() {
        String json = "{user-name:'noear',userName:'noear'}";
        NameModel nameModel = ONode.deserialize(json, NameModel.class);
        System.out.println(nameModel);
        assert "noear".equals(nameModel.getUserName());
    }

    @Test
    public void test7() {
        SModel sModel = new SModel();
        sModel.age = 11;
        sModel.name = "test";

        String json = ONode.from(sModel, Feature.Read_UseOnlyGetter, Feature.Write_UseOnlySetter).serialize();
        System.out.println(json);
        assert json.contains("name") == false;
        assert json.contains("age");
    }

    @Test
    public void test8() {
        String json = "{age:11,name:'test'}";

        SModel sModel = ONode.load(json, Feature.Read_UseOnlyGetter, Feature.Write_UseOnlySetter).to(SModel.class);
        System.out.println(sModel);

        assert sModel.name == null;
        assert sModel.age == 11;
    }


    @Test
    public void testb_10() {
        Set<String> sets = new HashSet<>();
        sets.add("1");
        sets.add("2");
        sets.add("3");

        String json = ONode.serialize(sets, Feature.Write_ClassName);
        System.out.println(json);

        Set<String> sets2 = ONode.deserialize(json, Set.class);
        System.out.println(ONode.serialize(sets2, Feature.Write_ClassName));

        assert sets2.size() == sets.size();
    }

    @Test
    public void testb_11() {
        Set<String> sets = new HashSet<>();
        sets.add("1");
        sets.add("2");
        sets.add("3");

        Options options = Options.enableOf(Feature.Write_ArrayClassName);

        String json = ONode.from(sets, options).serialize();
        System.out.println(json);

        Set<String> sets2 = ONode.load(json, options).to();
        System.out.println(ONode.from(sets2, options).serialize());

        assert sets2.size() == sets.size();
    }

    @Test
    public void testc_10() {
        FoodRestarurantHoursPageVO tmp = new FoodRestarurantHoursPageVO();
        tmp.setId(1L);
        tmp.setHoursName("entity");
        tmp.setDate(LocalDate.now());
        tmp.setEndTime(OffsetTime.now());
        tmp.setStartTime(OffsetTime.now());

        String json2 = ONode.from(tmp).serialize();
        System.out.println(json2);
    }

    @Test
    public void testd_10() {
        DTimeVO tmp = new DTimeVO();

        try {
            String json2 = ONode.from(tmp).serialize(Feature.Write_PrettyFormat);
            System.out.println(json2);
            assert false;
        } catch (UnsupportedTemporalTypeException e) {
            assert true;
        }
    }
}