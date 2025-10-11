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
import org.noear.snack4.util.Asserts;

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

        List<String> chunks = splitSelectors(segmentStr);

        for (String chunk : chunks) {
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
                    if (Asserts.isNumber(chunk)) {
                        selectors.add(new IndexSelector(chunk));
                    } else {
                        selectors.add(new NameSelector(chunk));
                    }
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
            selector.select(ctx, isDescendant(), currentNodes, result);
        }

        return result;
    }

    /**
     * 按顶层逗号分割选择器字符串，会忽略括号和方括号内的逗号。
     *
     * @param segmentStr 待分割的字符串，例如 "0, 'name', ?(@.price < 10 && @.category in ['books', 'fiction'])"
     * @return 分割后的选择器列表
     */
    public static List<String> splitSelectors(String segmentStr) {
        List<String> result = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        int parenLevel = 0;   // 圆括号 () 的嵌套层级
        int bracketLevel = 0; // 方括号 [] 的嵌套层级

        for (char c : segmentStr.toCharArray()) {
            if (c == ',' && parenLevel == 0 && bracketLevel == 0) {
                // 只有当逗号在最外层时，才进行分割
                result.add(currentChunk.toString().trim());
                currentChunk.setLength(0); // 重置 StringBuilder
            } else {
                // 更新嵌套层级
                if (c == '(') {
                    parenLevel++;
                } else if (c == ')') {
                    parenLevel--;
                } else if (c == '[') {
                    bracketLevel++;
                } else if (c == ']') {
                    bracketLevel--;
                }
                currentChunk.append(c);
            }
        }
        // 添加最后一个片段
        result.add(currentChunk.toString().trim());

        return result;
    }
}