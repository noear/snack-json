package features.snack4.codec_safety;

import demo.snack4._models.UserModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.node.Options;
import org.noear.snack4.node.SnackException;
import org.noear.snack4.codec.ObjectPatternCreator;

import java.util.*;

/**
 *
 * @author noear 2025/10/9 created
 *
 */
public class TypeSafety {
    @Test
    public void case1() {
        Options options = Options.of().addCreator(new ObjectPatternCreator<Object>() {
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
            ONode.ofJson("{id:1}", options).toBean(UserModel.class);
        });

        ONode.ofJson("{id:1}", options).toBean(Map.class);
    }

    @Test
    public void case2() {
        Options options = Options.of();

        options.addCreator(Map.class, (opts, node, clazz) -> new HashMap());
        options.addCreator(List.class, (opts, node, clazz) -> new ArrayList());

        options.addCreator(new ObjectPatternCreator<Object>() {
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
            ONode.ofJson("{id:1}", options).toBean(UserModel.class);
        });

        ONode.ofJson("{id:1}", options).toBean(Map.class);
    }
}