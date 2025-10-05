package demo.snack4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.noear.snack4.annotation.ONodeAttr;

/**
 * 用于单元测试枚举解析
 *
 * @author hans
 */
@ToString
@AllArgsConstructor
@Getter
public enum BookType {
    NOVEL(2,"小说"),
    CLASSICS(3,"名著"),
    ;

    @ONodeAttr
    public final int code;
    public final String des;
}
