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
import org.noear.snack4.jsonpath.JsonPathException;
import org.noear.snack4.jsonpath.QueryContext;
import org.noear.snack4.jsonpath.selector.ArraySliceSelector;
import org.noear.snack4.jsonpath.util.RangeUtil;
import org.noear.snack4.jsonpath.Segment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 数组切片选择器（如 $.list[1:4]，[1:5:1]）
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public class ArraySliceSegment implements Segment {
    ArraySliceSelector selector;

    public ArraySliceSegment(String segmentStr) {
       this.selector = new ArraySliceSelector(segmentStr);
    }

    @Override
    public String toString() {
        return "[" + selector.toString() + "]";
    }

    @Override
    public List<ONode> resolve(QueryContext ctx, List<ONode> currentNodes) {
        List<ONode> results = new ArrayList<>();
        selector.select(ctx, currentNodes, results);

        ctx.flattened = false;
        return results;
    }
}