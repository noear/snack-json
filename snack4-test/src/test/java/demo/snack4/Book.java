package demo.snack4;

import demo.snack4.enums.BookType;
import lombok.Data;

/**
 * 用于单元测试
 *
 * @author hans
 */
@Data
public class Book {
    private String name;
    private BookType dict;
}
