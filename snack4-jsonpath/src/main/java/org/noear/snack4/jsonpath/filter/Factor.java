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
package org.noear.snack4.jsonpath.filter;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.jsonpath.JsonPath;
import org.noear.snack4.util.Asserts;

/**
 * 条件因子描述
 *
 * @author noear 2025/10/10 created
 * @since 4.0
 */
public class Factor {
    private final String value;
    private ONode nodeValue;
    private JsonPath queryValue;

    public String getValue() {
        return value;
    }

    public ONode getNode() {
        return nodeValue;
    }

    public JsonPath getQuery() {
        return queryValue;
    }

    @Override
    public String toString() {
        return value;
    }

    public Factor(String value) {
        this.value = value;

        if (Asserts.isNotEmpty(value)) {
            char ch = value.charAt(0);

            if (ch == '@' || ch == '$') {
                queryValue = JsonPath.compile(value);
            } else {
                if (ch == '\'') {
                    //字符串
                    nodeValue = new ONode(Options.DEF_OPTIONS, value.substring(1, value.length() - 1));
                } else if (ch == '/') {
                    //正则
                    nodeValue = new ONode(Options.DEF_OPTIONS, value);
                } else {
                    //其它
                    nodeValue = ONode.ofJson(value);
                }
            }
        }

        if (nodeValue == null) {
            nodeValue = new ONode();
        }
    }
}
