package features.snack4.codec_safety;

import demo.snack4._models.UserModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.SnackException;
import org.noear.snack4.codec.ObjectPatternFactory;

import java.util.*;

/**
 *
 * @author noear 2025/10/9 created
 *
 */
public class TypeSafety {
    @Test
    public void case1() {
        Options options = Options.of().addFactory(new ObjectPatternFactory<Object>() {
            @Override
            public boolean calCreate(Class<?> clazz) {
                return true;
            }

            @Override
            public Object create(Options opts, ONode node, Class<?> clazz) {
                if (clazz == Map.class) {
                    return new HashMap<>();
                } else if (clazz == List.class) {
                    return new ArrayList<>();
                } else {
                    //如果返回 null，则交由框架实例化
                }

                throw new SnackException("");
            }
        });

        Assertions.assertThrows(SnackException.class, () -> {
            ONode.deserialize("{id:1}", UserModel.class, options);
        });

        ONode.deserialize("{id:1}", Map.class, options);
    }

    @Test
    public void case2() {
        Options options = Options.of();

        options.addFactory(Map.class, (opts, node, clazz) -> new HashMap());
        options.addFactory(List.class, (opts, node, clazz) -> new ArrayList());

        options.addFactory(new ObjectPatternFactory<Object>() {
            @Override
            public boolean calCreate(Class<?> clazz) {
                return true;
            }

            @Override
            public Object create(Options opts, ONode node, Class<?> clazz) {
                //如果返回 null，则交由框架实例化
                throw new SnackException("");
            }
        });

        Assertions.assertThrows(SnackException.class, () -> {
            ONode.deserialize("{id:1}", UserModel.class, options);
        });

        ONode.deserialize("{id:1}", Map.class, options);
    }
}
