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
package org.noear.snack4.jsonpath.util;

/**
 * 逻辑表达式项分析工具
 *
 * @author noear 2025/10/13 created
 * @since 4.0
 */
public class TermUtil {
    /**
     * 分析: 尝试将表达式分解为 [left, op, right]
     * 规则: left 必须有；op 和 right 可能同时没有；当有 op 时，right 必须有；op 中间可能有空隔。
     */
    public static String[] resolve(String expr) {
        String inputExpr = expr.trim();
        String leftStr;
        String opStr = null;
        String rightStr = null;

        int parenLevel = 0;   // 圆括号 () 层级
        int bracketLevel = 0; // 方括号 [] 层级
        int braceLevel = 0;   // 花括号 {} 层级
        char quoteChar = 0;   // 当前是否在引号内 (' 或 ")

        int endOfLeft = -1; // 左操作数结束位置的索引

        // 1. 寻找左操作数的结束位置 (第一个顶级空格)
        for (int i = 0; i < inputExpr.length(); i++) {
            char c = inputExpr.charAt(i);

            if (quoteChar != 0) {
                if (c == quoteChar) quoteChar = 0;
                continue;
            }

            switch (c) {
                case '\'':
                case '"':
                    quoteChar = c;
                    break;
                case '(': parenLevel++; break;
                case ')': if (parenLevel > 0) parenLevel--; break;
                case '[': bracketLevel++; break;
                case ']': if (bracketLevel > 0) bracketLevel--; break;
                case '{': braceLevel++; break;
                case '}': if (braceLevel > 0) braceLevel--; break;
                case ' ':
                    // 只有当所有层级为 0 时，空格才是顶级分隔符
                    if (parenLevel == 0 && bracketLevel == 0 && braceLevel == 0) {
                        endOfLeft = i;
                        i = inputExpr.length(); // 跳出循环
                    }
                    break;
            }
        }

        if (endOfLeft == -1) {
            // 2. 没有找到顶级空格，整个字符串是左操作数
            leftStr = inputExpr;
        } else {
            // 3. 分割出左操作数和剩余部分
            leftStr = inputExpr.substring(0, endOfLeft).trim();
            String opAndRight = inputExpr.substring(endOfLeft).trim();

            // 4. 在 opAndRight 中寻找操作符和右操作数之间的顶级空格

            // 重置状态机，只在 opAndRight 内部查找第一个顶级空格
            parenLevel = 0;
            bracketLevel = 0;
            braceLevel = 0;
            quoteChar = 0;

            int separatorIndex = -1; // 标记 op 和 right 之间的分隔符的索引

            // 由于操作符可能有多个词，我们需要找到最后一个在顶级状态下的空格
            // 例如 "op1 op2 op3 right" -> 我们需要找到 op3 和 right 之间的空格

            // 寻找 op 和 right 之间的最后一个顶级空格
            for (int i = opAndRight.length() - 1; i >= 0; i--) {
                char c = opAndRight.charAt(i);

                if (quoteChar != 0) {
                    if (c == quoteChar) quoteChar = 0;
                    continue;
                }

                // 注意：由于我们是从右向左扫描，所以层级计数是反向的
                switch (c) {
                    case '\'':
                    case '"':
                        quoteChar = c;
                        break;
                    case ')': parenLevel++; break;
                    case '(': if (parenLevel > 0) parenLevel--; break;
                    case ']': bracketLevel++; break;
                    case '[': if (bracketLevel > 0) bracketLevel--; break;
                    case '}': braceLevel++; break;
                    case '{': if (braceLevel > 0) braceLevel--; break;
                    case ' ':
                        // 只有当所有层级为 0 时，空格才是顶级分隔符
                        if (parenLevel == 0 && bracketLevel == 0 && braceLevel == 0) {
                            separatorIndex = i;
                            i = -1; // 跳出循环
                        }
                        break;
                }
            }

            if (separatorIndex == -1) {
                // 没有找到顶级空格，整个 opAndRight 都是操作符
                opStr = opAndRight;
                // rightStr 保持 null
            } else {
                // 找到了操作符和右操作数之间的分隔符 (最后一个顶级空格)
                opStr = opAndRight.substring(0, separatorIndex).trim();
                rightStr = opAndRight.substring(separatorIndex + 1).trim();
            }

            // 5. 修正 op 存在但 right 不存在的情况 (例如 'is null' 或解析错误)
            // 如果 rightStr 为空，则 opStr 必须包含整个 opAndRight，且 rightStr 必须为 null
            if (opStr != null && (rightStr == null || rightStr.isEmpty())) {
                // 此时 opStr 包含了整个 opAndRight，因为 rightStr 是空串或 null
                opStr = opAndRight;
                rightStr = null;
            }
        }

        return new String[]{leftStr, opStr, rightStr};
    }
}