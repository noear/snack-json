package demo.snack4._models;


import org.noear.snack4.node.Feature;
import org.noear.snack4.annotation.ONodeAttr;

public class AttrModel {
    public int id;
    @ONodeAttr(features = Feature.Write_NumbersAsString)
    public long traceId;
    public String name;

    @Override
    public String toString() {
        return "AttrModel{" +
                "id=" + id +
                ", traceId=" + traceId +
                ", name='" + name + '\'' +
                '}';
    }
}
