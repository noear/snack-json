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
import org.noear.snack4.jsonpath.selector.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择片段
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public class SelectSegment extends AbstractSegment {
    private final String segmentStr;
    private List<Selector> selectors = new ArrayList<>();

    public SelectSegment(String segmentStr) {
        this.segmentStr = segmentStr;

        for (String s : segmentStr.split(",")) {
            String chunk = s.trim();

            if (chunk.length() > 0) {
                char ch = chunk.charAt(0);

                if (ch == '*') {
                    selectors.add(new WildcardSelector());
                } else if (ch == '$' || ch == '@') {
                    selectors.add(new QuerySelector(chunk));
                } else if (ch == '?') {
                    selectors.add(new FilterSelector(chunk));
                } else if (ch == '\'') {
                    selectors.add(new NameSelector(chunk));
                } else if (chunk.indexOf(':') >= 0) {
                    selectors.add(new SliceSelector(chunk));
                } else {
                    selectors.add(new IndexSelector(chunk));
                }
            }
        }
    }

    @Override
    public String toString() {
        return "[" + segmentStr + "]";
    }

    @Override
    public List<ONode> resolve(QueryContext ctx, List<ONode> currentNodes) {
        List<ONode> result = new ArrayList<>();

        for (Selector selector : selectors) {
            selector.select(ctx, isFlattened(), currentNodes, result);
        }

        return result;
    }
}