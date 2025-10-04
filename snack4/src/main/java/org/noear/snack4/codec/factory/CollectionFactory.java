package org.noear.snack4.codec.factory;

import org.noear.snack4.Options;
import org.noear.snack4.codec.ObjectFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class CollectionFactory implements ObjectFactory<Collection> {
    @Override
    public Collection create(Options opts, Class<Collection> clazz) {
        return new ArrayList();
    }
}
