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
import org.noear.snack4.jsonpath.Context;
import org.noear.snack4.jsonpath.FunctionLib;
import org.noear.snack4.jsonpath.QueryMode;
import org.noear.snack4.jsonpath.SegmentFunction;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author noear
 * @since 4.0
 */
public class FunctionSegment implements SegmentFunction {
    private final String funcName;

    public FunctionSegment(String segmentStr) {
        this.funcName = segmentStr.substring(0, segmentStr.length() - 2);
    }

    @Override
    public List<ONode> resolve(List<ONode> currentNodes, Context context, QueryMode mode) {
        return Collections.singletonList(
                FunctionLib.get(funcName).apply(currentNodes) // 传入节点列表
        );
    }
}
