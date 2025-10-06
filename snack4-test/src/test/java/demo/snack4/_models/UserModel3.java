package demo.snack4._models;

import org.noear.snack4.Feature;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.ObjectEncoder;

import java.util.Date;

public class UserModel3 {
    public ObjectEncoder nodeEncoder;

    public int id;
    @ONodeAttr(deserialize = false, serialize = false)
    public String name;
    @ONodeAttr(deserialize = false, serialize = false)
    public String note;

    @ONodeAttr(format = "yyyyMMdd")
    public Date date;

    public String nullVal;

    @Override
    public String toString() {
        return "UserModel2{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", note='" + note + '\'' +
                ", date=" + date +
                '}';
    }
}
