package demo.snack3;

import demo.snack3.enums.BookType;
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
