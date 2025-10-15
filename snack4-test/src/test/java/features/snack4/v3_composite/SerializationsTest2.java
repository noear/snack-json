package features.snack4.v3_composite;

import demo.snack4._model4.QueryParamEntity;
import demo.snack4._models.*;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.node.Feature;
import org.noear.snack4.codec.TypeRef;

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
        return ONode.ofBean(buildObj()).toJson();
    }

    @Test
    public void test01() {
        String tmp = ONode.ofBean(buildObj(), Feature.Write_ClassName).toJson();
        System.out.println(tmp);
    }

    @Test
    public void test02() {
        String tmp = ONode.ofBean(buildObj(), Feature.Write_ClassName).toJson();
        tmp = tmp.replaceAll("UserGroupModel", "UserGroupModel2");
        UserGroupModel2 tmp2 = ONode.ofJson(tmp, Feature.Read_AutoType).toBean(UserGroupModel2.class);

        assert tmp2.users != null;
        assert tmp2.users.length > 2;
        System.out.println(tmp2);
    }

    @Test
    public void test10() throws Exception {
        String json0 = buildJson();

        System.out.println(json0);
        UserGroupModel group0 = ONode.ofJson(json0)
                .toBean((new TypeRef<UserGroupModel>() {
                }));

        assert group0.id == 9999;
    }

    @Test
    public void test11() throws Exception {
        String json0 = buildJson();

        System.out.println(json0);
        UserGroupModel group0 = ONode.ofJson(json0)
                .toBean(UserGroupModel.class);

        assert group0.id == 9999;
    }

    @Test
    public void test20() throws Exception {
        String json0 = buildJson();

        System.out.println(json0);
        List<UserModel> group0 = ONode.ofJson(json0).get("users")
                .toBean((new ArrayList<UserModel>() {
                }).getClass());

        assert group0.size() == 5;
    }

    @Test
    public void test21() throws Exception {
        String json0 = buildJson();

        System.out.println(json0);
        List<UserModel> group0 = ONode.ofJson(json0).get("users")
                .toBean((new TypeRef<List<UserModel>>() {}));

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

        ONode oNode = ONode.ofBean(props);

        System.out.println(oNode.toJson());

        QueryParamEntity entity = oNode.toBean(QueryParamEntity.class);

        assert entity != null;
        assert entity.getPageIndex() == 0;
        assert entity.getPageSize() == 10;
        assert entity.getSorts().size() > 0;
//        assert entity.getTerms().size() > 0;
//        assert entity.getExcludes().size() == 1;
    }

    @Test
    public void test4() {
        Properties properties = new Properties();
        properties.put("label","1");
        properties.put("users.a.name", "a");
        properties.put("users.user1.name", "user1");

        PersonColl tmp = ONode.ofBean(properties).toBean(PersonColl.class);

        assert tmp != null;
        assert tmp.getUsers() != null;
        assert tmp.getUsers().size() == 2;
        assert tmp.getUsers().get("user1") instanceof Person;
    }

    @Test
    public void test5() {
        String json = "{data:{a:1,b:2}}";
        MapModel mapModel = ONode.ofJson(json).toBean(MapModel.class);

        assert mapModel != null;
        assert mapModel.data != null;
        assert mapModel.data.size() == 2;
    }

    @Test
    public void test6() {
        String json = "{user-name:'noear',userName:'noear'}";
        NameModel nameModel = ONode.ofJson(json).toBean(NameModel.class);
        System.out.println(nameModel);
        assert "noear".equals(nameModel.getUserName());
    }

    @Test
    public void test7() {
        SModel sModel = new SModel();
        sModel.age = 11;
        sModel.name = "test";

        String json = ONode.ofBean(sModel, Feature.Read_OnlyUseGetter, Feature.Write_OnlyUseSetter).toJson();
        System.out.println(json);
        assert json.contains("name") == false;
        assert json.contains("age");
    }

    @Test
    public void test8() {
        String json = "{age:11,name:'test'}";

        SModel sModel = ONode.ofJson(json, Feature.Read_OnlyUseGetter, Feature.Write_OnlyUseSetter).toBean(SModel.class);
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

        String json = ONode.ofBean(sets, Feature.Write_ClassName).toJson();
        System.out.println(json);

        Set<String> sets2 = ONode.ofJson(json).toBean(Set.class);
        System.out.println(ONode.ofBean(sets2, Feature.Write_ClassName).toJson());

        assert sets2.size() == sets.size();
    }

    @Test
    public void testb_11() {
        Set<String> sets = new HashSet<>();
        sets.add("1");
        sets.add("2");
        sets.add("3");

        String json = ONode.ofBean(sets).toJson();
        System.out.println(json);

        Set<String> sets2 = ONode.ofJson(json).toBean(Set.class);
        System.out.println(ONode.ofBean(sets2).toJson());

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

        String json2 = ONode.ofBean(tmp).toJson();
        System.out.println(json2);
    }

    @Test
    public void testd_10() {
        DTimeVO tmp = new DTimeVO();

        try {
            String json2 = ONode.ofBean(tmp, Feature.Write_PrettyFormat).toJson();
            System.out.println(json2);
            assert false;
        } catch (UnsupportedTemporalTypeException e) {
            assert true;
        }
    }
}