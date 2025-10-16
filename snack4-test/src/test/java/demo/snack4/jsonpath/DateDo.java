package demo.snack4.jsonpath;

import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.annotation.ONodeCreator;

import java.util.Date;

/**
 *
 * @author noear 2025/10/16 created
 *
 */
public class DateDo {
    public Date date1 = new Date();

    @ONodeAttr(format="yyyy-MM-dd")
    public Date date2 = new Date();

    public DateDo() {
    }

    public DateDo(Date date1) {
        this.date1 = date1;
    }

    @ONodeCreator
    public DateDo(Date date1, Date date2) {
        this.date1 = date1;
        this.date2 = date2;
    }
}