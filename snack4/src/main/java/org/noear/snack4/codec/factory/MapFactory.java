package org.noear.snack4.codec.factory;

import org.noear.snack4.Options;
import org.noear.snack4.codec.ObjectFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class MapFactory implements ObjectFactory<Map> {
    @Override
    public Map create(Options opts, Class<Map> clazz) {
        return new LinkedHashMap();
    }
}
