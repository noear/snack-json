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
 * 逻辑表达式项分析工具。
 * 采用字符级扫描和层级保护机制，实现高性能、高通用性的表达式项（Term）解析。
 *
 * @author noear 2025/10/13 created
 * @since 4.0
 */
public class TermUtil {
    /**
     * 分析表达式项，将其分解为 [left, op, right] 三部分。
     * * 解析规则：
     * 1. 表达式（Term）必须有 left (左操作数)。
     * 2. op (操作符) 和 right (右操作数) 可以同时缺失（如函数表达式）。
     * 3. 如果 op 存在，则 right 必须存在（特殊情况如 'is null'，解析逻辑会将其 right 置为 null）。
     * 4. op 可能是多词操作符，中间包含空格（如 "starts with", "not like"）。
     * 5. 通过括号、方括号、花括号和引号层级保护，确保解析边界的准确性。
     *
     * @param expr 待解析的表达式字符串。
     * @return 包含 [leftStr, opStr, rightStr] 三个元素的字符串数组。
     */
    public static String[] resolve(String expr) {
        int inputLength = expr.length();
        String leftStr;
        String opStr = null;
        String rightStr = null;

        // 层级状态变量，用于保护表达式内部的空格
        int parenLevel = 0;   // 圆括号 () 层级
        int bracketLevel = 0; // 方括号 [] 层级
        int braceLevel = 0;   // 花括号 {} 层级
        char quoteChar = 0;   // 当前激活的引号类型 (' 或 ")

        int endOfLeft = -1; // 左操作数结束（第一个顶级空格）

        // 1. 正向扫描：寻找 leftStr 和 op/right 的分隔符 (第一个顶级空格)
        for (int i = 0; i < inputLength; i++) {
            char c = expr.charAt(i);

            // 处于引号内时，忽略所有字符，直到引号结束
            if (quoteChar != 0) {
                if (c == quoteChar) quoteChar = 0;
                continue;
            }

            switch (c) {
                case '\'':
                case '"':
                    quoteChar = c;
                    break;
                case '(':
                    parenLevel++;
                    break;
                case ')':
                    if (parenLevel > 0) parenLevel--;
                    break;
                case '[':
                    bracketLevel++;
                    break;
                case ']':
                    if (bracketLevel > 0) bracketLevel--;
                    break;
                case '{':
                    braceLevel++;
                    break;
                case '}':
                    if (braceLevel > 0) braceLevel--;
                    break;
                case ' ':
                    // 顶级空格是分隔符
                    if (parenLevel == 0 && bracketLevel == 0 && braceLevel == 0) {
                        endOfLeft = i;
                        i = inputLength; // 跳出循环
                    }
                    break;
            }
        }

        if (endOfLeft == -1) {
            // 表达式中没有顶级空格，整个字符串是左操作数（如函数调用或无操作符表达式）
            leftStr = expr;
        } else {
            // 3. 分割出左操作数
            leftStr = expr.substring(0, endOfLeft);

            // 确定操作符/右操作数的起始索引（跳过 leftStr 后的所有空格）
            int startOfOp = endOfLeft;
            while (startOfOp < inputLength && expr.charAt(startOfOp) == ' ') {
                startOfOp++;
            }

            // 如果操作符/右操作数部分为空，则返回
            if (startOfOp == inputLength) {
                return new String[]{leftStr, null, null};
            }

            // 4. 反向扫描：寻找操作符 op 和 右操作数 right 之间的最后一个顶级空格
            // 这个空格就是 op 和 right 的边界，它允许 op 是多词操作符
            parenLevel = 0;
            bracketLevel = 0;
            braceLevel = 0;
            quoteChar = 0;
            int separatorIndex = -1; // 记录 op 和 right 边界的空格索引 (相对于 inputExpr)

            for (int i = inputLength - 1; i >= startOfOp; i--) {
                char c = expr.charAt(i);

                // 反向层级保护（从右到左）
                if (quoteChar != 0) {
                    if (c == quoteChar) quoteChar = 0;
                    continue;
                }
                switch (c) {
                    case '\'':
                    case '"':
                        quoteChar = c;
                        break;
                    case ')':
                        parenLevel++;
                        break;
                    case '(':
                        if (parenLevel > 0) parenLevel--;
                        break;
                    case ']':
                        bracketLevel++;
                        break;
                    case '[':
                        if (bracketLevel > 0) bracketLevel--;
                        break;
                    case '}':
                        braceLevel++;
                        break;
                    case '{':
                        if (braceLevel > 0) braceLevel--;
                        break;
                    case ' ':
                        if (parenLevel == 0 && bracketLevel == 0 && braceLevel == 0) {
                            separatorIndex = i;
                            i = startOfOp - 1; // 跳出循环
                        }
                        break;
                }
            }

            if (separatorIndex == -1) {
                // 没有找到 op 和 right 的分隔空格，整个剩余部分都是操作符 op
                opStr = expr.substring(startOfOp);
            } else {
                // 找到 opStr 的精确结束索引（separatorIndex 之前的非空格字符）
                int endOfOp = separatorIndex - 1;
                while (endOfOp >= startOfOp && expr.charAt(endOfOp) == ' ') {
                    endOfOp--;
                }

                // 找到 rightStr 的精确起始索引（separatorIndex 之后的非空格字符）
                int startOfRight = separatorIndex + 1;
                while (startOfRight < inputLength && expr.charAt(startOfRight) == ' ') {
                    startOfRight++;
                }

                // 使用精确索引截取 opStr
                if (endOfOp >= startOfOp) {
                    opStr = expr.substring(startOfOp, endOfOp + 1);
                }

                // 使用精确索引截取 rightStr
                if (startOfRight < inputLength) {
                    rightStr = expr.substring(startOfRight);
                }

                // 修正：如果解析后 rightStr 为空或不存在，则认为整个剩余部分都是 op
                if (opStr != null && (rightStr == null || rightStr.isEmpty())) {
                    opStr = expr.substring(startOfOp);
                    rightStr = null;
                }
            }
        }

        return new String[]{leftStr, opStr, rightStr};
    }
}