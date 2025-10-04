package features.snack3.test1.enums;

/**
 * @Author kevin
 * @Date 2022-10-02 20:17
 * @Description
 */
public enum EnumSuper implements DictEnum{
  A("1","父类属性测试A"),
  B("2","父类属性测试B"),
  ;

  private String code;

  private String display;

  EnumSuper(String code, String display) {
    this.code = code;
    this.display = display;
  }

  @Override
  public String getDisplay() {
    return this.display;
  }

  @Override
  public String getCode() {
    return this.code;
  }
}
