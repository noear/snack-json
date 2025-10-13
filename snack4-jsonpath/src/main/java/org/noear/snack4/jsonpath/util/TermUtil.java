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

        int parenLevel = 0;   // () 层级
        int bracketLevel = 0; // [] 层级
        int braceLevel = 0;   // {} 层级
        char quoteChar = 0;   // 引号 ' 或 "

        int endOfLeft = -1; // 左操作数结束位置的索引

        // 1. 正向扫描：寻找左操作数的结束位置 (第一个顶级空格)
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
                        i = inputExpr.length();
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

            // 4. 反向扫描：在 opAndRight 中寻找操作符和右操作数之间的最后一个顶级空格

            // 重置状态机，开始反向扫描
            parenLevel = 0;
            bracketLevel = 0;
            braceLevel = 0;
            quoteChar = 0;

            int separatorIndex = -1; // 标记 op 和 right 之间的分隔符的索引

            for (int i = opAndRight.length() - 1; i >= 0; i--) {
                char c = opAndRight.charAt(i);

                if (quoteChar != 0) {
                    if (c == quoteChar) quoteChar = 0;
                    continue;
                }

                // 注意：反向扫描时，层级计数是反向的
                switch (c) {
                    case '\'':
                    case '"':
                        quoteChar = c;
                        break;
                    case ')': parenLevel++; break;
                    case '(': if (parenLevel > 0) parenLevel--; break; // <--- 健壮性优化点
                    case ']': bracketLevel++; break;
                    case '[': if (bracketLevel > 0) bracketLevel--; break; // <--- 健壮性优化点
                    case '}': braceLevel++; break;
                    case '{': if (braceLevel > 0) braceLevel--; break; // <--- 健壮性优化点
                    case ' ':
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
            } else {
                // 找到了操作符和右操作数之间的分隔符 (最后一个顶级空格)
                opStr = opAndRight.substring(0, separatorIndex).trim();
                rightStr = opAndRight.substring(separatorIndex + 1).trim();

                // 修正：如果 opStr 存在但 rightStr 是空串（例如：'is null' 或 'op '）
                if (rightStr.isEmpty()) {
                    opStr = opAndRight;
                    rightStr = null;
                }
            }
        }

        return new String[]{leftStr, opStr, rightStr};
    }
}