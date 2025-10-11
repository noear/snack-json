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
package org.noear.snack4.jsonpath.segment;


import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 名称选择器：选择对象的命名子对象（如 $.demo, $.demo.user）
 *
 * @author noear
 * @since 4.0
 */
public class NameSegment implements Segment {
    private final String segmentStr;
    private String key;

    public NameSegment(String key) {
        this.segmentStr = key;
        this.key = key;
    }

    @Override
    public String toString() {
        return "['" + segmentStr + "']";
    }

    @Override
    public List<ONode> resolve(QueryContext ctx, List<ONode> currentNodes) {
        List<ONode> result = new ArrayList<>();

        for (ONode n : currentNodes) {
            IndexUtil.forKey(ctx, n, key, result);
        }

        ctx.flattened = false;
        return result;
    }
}
