package features.snack4.codec;

import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author noear 2025/10/15 created
 *
 */
public class FeatureMarkTest {
    @Test
    public void case1() {
        Set<Long> sets = new HashSet<>();

        for (Feature feature : Feature.values()) {
            System.out.println(feature.ordinal() + ", " + feature.mask() + "," + feature.name());

            if (sets.contains(feature.mask())) {
                assert false;
            } else {
                sets.add(feature.mask());
            }
        }
    }
}