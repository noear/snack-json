package org.noear.snack4.jsonpath;

import org.noear.snack4.ONode;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class JsonPathProviderImpl implements JsonPathProvider {
    /**
     * 根据 jsonpath 查询
     */
    public ONode select(ONode root, String path) {
        return JsonPath.compile(path).select(root);
    }

    /**
     * 根据 jsonpath 生成
     */
    public ONode create(ONode root, String path) {
        return JsonPath.compile(path).create(root);
    }

    /**
     * 根据 jsonpath 删除
     */
    public void delete(ONode root, String path) {
        JsonPath.compile(path).delete(root);
    }
}
