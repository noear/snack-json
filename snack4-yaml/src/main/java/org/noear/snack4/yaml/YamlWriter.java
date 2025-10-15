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
package org.noear.snack4.yaml;

import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.codec.util.DateUtil;
import org.noear.snack4.util.Asserts;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * YAML 书写器（严格支持 YAML 1.2.2 标准）
 *
 * @author noear 2025/3/16 created
 * @since 4.0
 */
public class YamlWriter {
    public static String write(ONode node, Options opts) throws IOException {
        StringWriter writer = new StringWriter();
        write(node, opts, writer);
        return writer.toString();
    }

    public static void write(ONode node, Options opts, Writer writer) throws IOException {
        new YamlWriter(opts, writer).write(node);
    }

    /// ////////////

    private final Options opts;
    private final Writer writer;
    private int depth = 0;
    private boolean inFlow = false;
    private StringBuilder stringBuilder;

    private StringBuilder getStringBuilder() {
        if (stringBuilder == null) {
            stringBuilder = new StringBuilder(32);
        } else {
            stringBuilder.setLength(0);
        }
        return stringBuilder;
    }

    public YamlWriter(Options opts, Writer writer) {
        Objects.requireNonNull(writer, "writer");

        this.writer = writer;
        this.opts = opts == null ? Options.DEF_OPTIONS : opts;
    }

    public void write(ONode node) throws IOException {
        writeDocument(node);
    }

    private void writeDocument(ONode node) throws IOException {
        // 写入文档开始标记
        writer.write("---\n");

        writeNode(node, false);

        // 写入文档结束标记（可选）
        if (depth == 0) {
            writer.write("\n...");
        }
    }

    private void writeNode(ONode node, boolean isMapValue) throws IOException {
        switch (node.getType()) {
            case Object:
                writeObject(node.getObject(), isMapValue);
                break;
            case Array:
                writeArray(node.getArray(), isMapValue);
                break;
            case String:
                writeString(node.getString(), isMapValue);
                break;
            case Number:
                writeNumber(node.getNumber(), isMapValue);
                break;
            case Date:
                writeDate(node.getDate(), isMapValue);
                break;
            case Boolean:
                writeBoolean(node.getBoolean(), isMapValue);
                break;
            case Null:
            case Undefined:
                writeNull(isMapValue);
                break;
        }
    }

    private void writeObject(Map<String, ONode> map, boolean isMapValue) throws IOException {
        if (map.isEmpty()) {
            writer.write("{}");
            return;
        }

        boolean useFlowStyle = shouldUseFlowStyle(map.size());
        boolean wasInFlow = inFlow;

        if (useFlowStyle) {
            inFlow = true;
            writer.write('{');
        } else {
            if (!isMapValue && depth > 0) {
                writer.write('\n');
            }
        }

        depth++;
        boolean first = true;

        for (Map.Entry<String, ONode> entry : map.entrySet()) {
            if (entry.getValue().isNull() && !opts.hasFeature(Feature.Write_Nulls)) {
                continue;
            }

            if (!first) {
                if (useFlowStyle) {
                    writer.write(',');
                    if (opts.hasFeature(Feature.Write_PrettyFormat)) {
                        writer.write(' ');
                    }
                }
            }

            if (useFlowStyle) {
                writeKey(entry.getKey());
                writer.write(':');
                if (opts.hasFeature(Feature.Write_PrettyFormat)) {
                    writer.write(' ');
                }
                writeNode(entry.getValue(), true);
            } else {
                writeIndentation();
                writeKey(entry.getKey());
                writer.write(':');
                if (!entry.getValue().isObject() && !entry.getValue().isArray()) {
                    writer.write(' ');
                }
                writeNode(entry.getValue(), true);
            }

            first = false;
        }

        depth--;

        if (useFlowStyle) {
            writer.write('}');
            inFlow = wasInFlow;
        }
    }

    private void writeArray(List<ONode> list, boolean isMapValue) throws IOException {
        if (list.isEmpty()) {
            writer.write("[]");
            return;
        }

        boolean useFlowStyle = shouldUseFlowStyle(list.size());
        boolean wasInFlow = inFlow;

        if (useFlowStyle) {
            inFlow = true;
            writer.write('[');
        } else {
            if (!isMapValue && depth > 0) {
                writer.write('\n');
            }
        }

        depth++;
        boolean first = true;

        for (ONode item : list) {
            if (!first) {
                if (useFlowStyle) {
                    writer.write(',');
                    if (opts.hasFeature(Feature.Write_PrettyFormat)) {
                        writer.write(' ');
                    }
                }
            }

            if (useFlowStyle) {
                writeNode(item, true);
            } else {
                writeIndentation();
                writer.write("- ");
                writeNode(item, false);
            }

            first = false;
        }

        depth--;

        if (useFlowStyle) {
            writer.write(']');
            inFlow = wasInFlow;
        }
    }

    private void writeString(String s, boolean isMapValue) throws IOException {
        if (needsQuotes(s)) {
            writeQuotedString(s, '"');
        } else if (containsSpecialCharacters(s)) {
            writeQuotedString(s, '\'');
        } else if (isMultiLineString(s)) {
            writeBlockString(s);
        } else {
            writer.write(escapeString(s));
        }
    }

    private void writeNumber(Number num, boolean isMapValue) throws IOException {
        if (opts.hasFeature(Feature.Write_NumbersAsString)) {
            writeQuotedString(num.toString(), '"');
        } else if (opts.hasFeature(Feature.Write_BigNumbersAsString) && Asserts.isBigNumber(num)) {
            writeQuotedString(num.toString(), '"');
        } else if (opts.hasFeature(Feature.Write_LongAsString) && num instanceof Long) {
            writeQuotedString(num.toString(), '"');
        } else {
            writer.write(num.toString());

            if (opts.hasFeature(Feature.Write_NumberTypeSuffix)) {
                if (num instanceof Double) {
                    writer.write('D');
                } else if (num instanceof Float) {
                    writer.write('F');
                } else if (num instanceof Long) {
                    writer.write('L');
                }
            }
        }
    }

    private void writeDate(java.util.Date date, boolean isMapValue) throws IOException {
        if (opts.hasFeature(Feature.Write_UseDateFormat)) {
            String dateStr = DateUtil.format(date, opts.getDateFormat());
            writeString(dateStr, isMapValue);
        } else {
            writeNumber(date.getTime(), isMapValue);
        }
    }

    private void writeBoolean(boolean value, boolean isMapValue) throws IOException {
        writer.write(value ? "true" : "false");
    }

    private void writeNull(boolean isMapValue) throws IOException {
        writer.write("null");
    }

    private void writeKey(String key) throws IOException {
        if (needsQuotes(key) || containsSpecialCharacters(key)) {
            writeQuotedString(key, '"');
        } else {
            writer.write(escapeString(key));
        }
    }

    private void writeQuotedString(String s, char quoteChar) throws IOException {
        writer.write(quoteChar);
        writeEscapedContent(s, quoteChar);
        writer.write(quoteChar);
    }

    private void writeBlockString(String s) throws IOException {
        String[] lines = s.split("\n", -1);

        if (lines.length == 1) {
            // 单行字符串，使用普通格式
            writer.write(escapeString(s));
            return;
        }

        // 使用字面块标量
        writer.write("|");

        // 检查是否需要去除末尾空行
        boolean stripTrailing = !s.endsWith("\n");
        if (stripTrailing) {
            writer.write("-");
        }

        writer.write("\n");

        depth++;
        for (String line : lines) {
            writeIndentation();
            writer.write(escapeString(line));
            writer.write("\n");
        }
        depth--;
    }

    private void writeEscapedContent(String s, char quoteChar) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            switch (c) {
                case '\n':
                    writer.write("\\n");
                    break;
                case '\r':
                    writer.write("\\r");
                    break;
                case '\t':
                    writer.write("\\t");
                    break;
                case '\b':
                    writer.write("\\b");
                    break;
                case '\f':
                    writer.write("\\f");
                    break;
                case '\\':
                    writer.write("\\\\");
                    break;
                default:
                    if (c == quoteChar) {
                        writer.write('\\');
                        writer.write(c);
                    } else if (c < 0x20) {
                        writer.write(String.format("\\u%04x", (int) c));
                    } else if (opts.hasFeature(Feature.Write_BrowserCompatible) && c > 0x7F) {
                        writer.write(String.format("\\u%04x", (int) c));
                    } else {
                        writer.write(c);
                    }
            }
        }
    }

    private String escapeString(String s) {
        // 对于普通字符串，只需要转义特殊字符
        StringBuilder sb = getStringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\') {
                sb.append("\\\\");
            } else if (c == '\"') {
                sb.append("\\\"");
            } else if (c == '\n') {
                sb.append("\\n");
            } else if (c == '\r') {
                sb.append("\\r");
            } else if (c == '\t') {
                sb.append("\\t");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private void writeIndentation() throws IOException {
        if (!inFlow && depth > 0) {
            for (int i = 0; i < depth; i++) {
                writer.write(opts.getWriteIndent());
            }
        }
    }

    private boolean shouldUseFlowStyle(int size) {
        // 对于小型的对象或数组，使用流式风格
        // 对于大型的或嵌套深的，使用块式风格
        return size <= 3 && depth < 3;
    }

    private boolean needsQuotes(String s) {
        if (s.isEmpty()) {
            return true;
        }

        // 检查是否包含需要引号的字符
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (i == 0) {
                // 首字符限制更严格
                if ("-?:[]{}#&*!|>\"'%@`".indexOf(c) >= 0 || Character.isWhitespace(c)) {
                    return true;
                }
            } else {
                // 其他位置
                if (":[]{}#,".indexOf(c) >= 0 || Character.isWhitespace(c)) {
                    return true;
                }
            }

            // 检查控制字符
            if (c < 0x20) {
                return true;
            }
        }

        // 检查是否为可能被误解的值
        if (isAmbiguousValue(s)) {
            return true;
        }

        return false;
    }

    private boolean isAmbiguousValue(String s) {
        // 这些值在 YAML 中可能有特殊含义，需要引号
        switch (s) {
            case "null":
            case "Null":
            case "NULL":
            case "true":
            case "True":
            case "TRUE":
            case "false":
            case "False":
            case "FALSE":
            case "yes":
            case "Yes":
            case "YES":
            case "no":
            case "No":
            case "NO":
            case "on":
            case "On":
            case "ON":
            case "off":
            case "Off":
            case "OFF":
            case "~":
            case ".inf":
            case ".Inf":
            case ".INF":
            case ".nan":
            case ".NaN":
            case ".NAN":
            case "-.inf":
            case "-.Inf":
            case "-.INF":
            case "+.inf":
            case "+.Inf":
            case "+.INF":
                return true;
        }

        // 检查数字格式（避免被解析为数字）
        if (s.matches("^[-+]?[0-9]") &&
                (s.matches("^[-+]?[0-9]+$") ||
                        s.matches("^[-+]?[0-9]*\\.[0-9]+$") ||
                        s.matches("^[-+]?[0-9]+(\\.[0-9]*)?[eE][-+]?[0-9]+$") ||
                        s.matches("^[-+]?\\.[0-9]+$"))) {
            return true;
        }

        return false;
    }

    private boolean containsSpecialCharacters(String s) {
        // 检查是否包含需要引号的特殊字符
        return s.chars().anyMatch(c ->
                c == ':' || c == '{' || c == '}' || c == '[' || c == ']' ||
                        c == ',' || c == '&' || c == '*' || c == '#' || c == '?' ||
                        c == '|' || c == '-' || c == '<' || c == '>' || c == '=' ||
                        c == '!' || c == '%' || c == '@' || c == '`' ||
                        Character.isWhitespace(c)
        );
    }

    private boolean isMultiLineString(String s) {
        return s.contains("\n") && s.length() > 40; // 长度阈值，避免短的多行字符串使用块标量
    }

    /**
     * 去除字符串末尾的空白字符（Java 8 兼容的 stripTrailing 替代）
     */
    private static String stripTrailing(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        int length = str.length();
        int end = length;

        // 从字符串末尾向前查找第一个非空白字符的位置
        while (end > 0 && Character.isWhitespace(str.charAt(end - 1))) {
            end--;
        }

        return end == length ? str : str.substring(0, end);
    }
}