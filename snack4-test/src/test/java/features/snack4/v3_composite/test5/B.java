package features.snack4.v3_composite.test5;

import java.util.List;

/**
 * @author noear 2023/5/10 created
 */
public class B {
    private List<String> bList;

    public B(List<String> bList) {
        this.bList = bList;
    }

    public List<String> getbList() {
        return bList;
    }

    public void setbList(List<String> bList) {
        this.bList = bList;
    }

    @Override
    public String toString() {
        return "B{" +
                "bList=" + bList +
                '}';
    }
}
