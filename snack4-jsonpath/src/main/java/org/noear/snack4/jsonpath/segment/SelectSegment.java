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
import org.noear.snack4.jsonpath.util.SelectUtil;
import org.noear.snack4.core.util.Asserts;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择片段
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public class SelectSegment extends AbstractSegment {
    private final String description;
    private List<Selector> selectors = new ArrayList<>();
    private boolean isMultiple;
    private boolean isExpanded;

    public SelectSegment(String description) {
        this.description = description;

        List<String> chunks = SelectUtil.splitSelectors(description);

        for (String chunk : chunks) {
            if (chunk.length() > 0) {
                Selector selector = null;

                char ch = chunk.charAt(0);
                if (ch == '*') {
                    selector = new WildcardSelector();
                } else if (ch == '$' || ch == '@') {
                    selector = new QuerySelector(chunk);
                } else if (ch == '?') {
                    selector = new FilterSelector(chunk);
                } else if (ch == '\'') {
                    selector = new NameSelector(chunk);
                } else if (chunk.indexOf(':') >= 0) {
                    selector = new SliceSelector(chunk);
                } else {
                    if (Asserts.isNumber(chunk)) {
                        selector = new IndexSelector(chunk);
                    } else {
                        selector = new NameSelector(chunk);
                    }
                }

                if (selector != null) {
                    isMultiple = isMultiple || selector.isMultiple();
                    isExpanded = isExpanded || selector.isExpanded();
                    selectors.add(selector);
                }
            }
        }
    }


    @Override
    public String toString() {
        return "[" + description + "]";
    }

    @Override
    public boolean isMultiple() {
        return isMultiple;
    }

    @Override
    public boolean isExpanded() {
        return isExpanded;
    }

    @Override
    public List<ONode> resolve(QueryContext ctx, List<ONode> currentNodes) {
        List<ONode> result = new ArrayList<>();

        for (Selector selector : selectors) {
            selector.select(ctx, isDescendant(), currentNodes, result);
        }

        return result;
    }
}