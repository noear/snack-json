package features.snack4.composite.test1;

import features.snack4.composite.test1.enums.EnumComp;
import features.snack4.composite.test1.enums.EnumMulti;
import features.snack4.composite.test1.enums.EnumSub;
import features.snack4.composite.test1.enums.EnumSuper;
import features.snack4.composite.test1.pojo.CompC;
import features.snack4.composite.test1.pojo.SubB;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Feature;

/**
 * @Author kevin
 * @Date 2022-10-02 20:21
 * @Description
 */
public class SnackTest {

  @Test
  public void testEnum() {
    CompC compC = new CompC();
    compC.setCompName("复合属性");
    compC.setCompEnumOne(EnumComp.A);


    SubB subB = new SubB();
    subB.setSubName("子类");
    subB.setCompEnum(EnumComp.A);
    subB.setSubbEnum(EnumSub.B);
    subB.setSuperEnum(EnumSuper.A);
    subB.setMultiEnum(EnumMulti.A);

    subB.setCompC(compC);


    System.out.println(ONode.serialize(subB));
    System.out.println("\n");
    System.out.println(ONode.from(subB, Feature.Write_EnumUsingName));
  }
}
