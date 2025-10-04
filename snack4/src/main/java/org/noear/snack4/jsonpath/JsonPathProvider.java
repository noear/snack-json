package org.noear.snack4.jsonpath;

import org.noear.snack4.ONode;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public interface JsonPathProvider {
    ONode select(ONode root, String path);

    ONode create(ONode root, String path);

    void delete(ONode root, String path);
}
