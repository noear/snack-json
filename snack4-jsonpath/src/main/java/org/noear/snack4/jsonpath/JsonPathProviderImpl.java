/*
 * Copyright 2005-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.snack4.jsonpath;

import org.noear.snack4.ONode;

/**
 *
 * @author noear 2025/10/3 created
 * @since 4.0
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
