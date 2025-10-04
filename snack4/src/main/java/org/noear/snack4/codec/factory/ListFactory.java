package org.noear.snack4.codec.factory;

import org.noear.snack4.Options;
import org.noear.snack4.codec.ObjectFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class ListFactory implements ObjectFactory<List> {
    @Override
    public List create(Options opts, Class<List> clazz) {
        return new ArrayList();
    }
}
