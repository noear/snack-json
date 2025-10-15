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
    private int indentLevel = 0;
    private final String indent;

    public YamlWriter(Options opts, Writer writer) {
        Objects.requireNonNull(writer, "writer");

        this.writer = writer;
        this.opts = opts == null ? Options.DEF_OPTIONS : opts;
        this.indent = opts.getWriteIndent();
    }

    public void write(ONode node) throws IOException {
        writeDocument(node);
    }

    private void writeDocument(ONode node) throws IOException {
        // 写入文档开始标记
        writer.write("---\n");

        writeNode(node, false, false);

        // 写入文档结束标记（可选）
        // writer.write("\n...");
    }

    private void writeNode(ONode node, boolean isSequenceItem, boolean isMapValue) throws IOException {
        if (node == null || node.isUndefined()) {
            writeNull(isSequenceItem, isMapValue);
            return;
        }

        switch (node.nodeType()) {
            case Object:
                writeObject(node.getObject(), isSequenceItem, isMapValue);
                break;
            case Array:
                writeArray(node.getArray(), isSequenceItem, isMapValue);
                break;
            case String:
                writeString(node.getString(), isSequenceItem, isMapValue);
                break;
            case Number:
                writeNumber(node.getNumber(), isSequenceItem, isMapValue);
                break;
            case Date:
                writeDate(node.getDate(), isSequenceItem, isMapValue);
                break;
            case Boolean:
                writeBoolean(node.getBoolean(), isSequenceItem, isMapValue);
                break;
            case Null:
                writeNull(isSequenceItem, isMapValue);
                break;
            case Undefined:
                writeNull(isSequenceItem, isMapValue);
                break;
        }
    }

    private void writeObject(Map<String, ONode> map, boolean isSequenceItem, boolean isMapValue) throws IOException {
        if (map.isEmpty()) {
            writer.write("{}");
            return;
        }

        boolean useFlowStyle = shouldUseFlowStyle();

        if (useFlowStyle) {
            writeFlowObject(map);
        } else {
            writeBlockObject(map, isSequenceItem, isMapValue);
        }
    }

    private void writeFlowObject(Map<String, ONode> map) throws IOException {
        writer.write('{');
        boolean first = true;

        for (Map.Entry<String, ONode> entry : map.entrySet()) {
            if (entry.getValue().isNull() && !opts.hasFeature(Feature.Write_Nulls)) {
                continue;
            }

            if (!first) {
                writer.write(',');
                if (opts.hasFeature(Feature.Write_PrettyFormat)) {
                    writer.write(' ');
                }
            }

            writeKey(entry.getKey(), true);
            writer.write(':');
            if (opts.hasFeature(Feature.Write_PrettyFormat)) {
                writer.write(' ');
            }
            writeNode(entry.getValue(), false, true);

            first = false;
        }

        writer.write('}');
    }

    private void writeBlockObject(Map<String, ONode> map, boolean isSequenceItem, boolean isMapValue) throws IOException {
        if (!isMapValue && !isSequenceItem && indentLevel > 0) {
            writer.write("\n");
        }

        indentLevel++;
        boolean first = true;

        for (Map.Entry<String, ONode> entry : map.entrySet()) {
            if (entry.getValue().isNull() && !opts.hasFeature(Feature.Write_Nulls)) {
                continue;
            }

            if (!first) {
                writer.write("\n");
            }

            writeIndentation();
            writeKey(entry.getKey(), false);
            writer.write(':');

            ONode value = entry.getValue();
            if (value.isObject() || value.isArray()) {
                writer.write("\n");
                writeNode(value, false, true);
            } else {
                writer.write(' ');
                writeNode(value, false, true);
            }

            first = false;
        }

        indentLevel--;
    }

    private void writeArray(List<ONode> list, boolean isSequenceItem, boolean isMapValue) throws IOException {
        if (list.isEmpty()) {
            writer.write("[]");
            return;
        }

        boolean useFlowStyle = shouldUseFlowStyle();

        if (useFlowStyle) {
            writeFlowArray(list);
        } else {
            writeBlockArray(list, isSequenceItem, isMapValue);
        }
    }

    private void writeFlowArray(List<ONode> list) throws IOException {
        writer.write('[');
        boolean first = true;

        for (ONode item : list) {
            if (!first) {
                writer.write(',');
                if (opts.hasFeature(Feature.Write_PrettyFormat)) {
                    writer.write(' ');
                }
            }

            writeNode(item, true, false);
            first = false;
        }

        writer.write(']');
    }

    private void writeBlockArray(List<ONode> list, boolean isSequenceItem, boolean isMapValue) throws IOException {
        if (!isMapValue && !isSequenceItem && indentLevel > 0) {
            writer.write("\n");
        }

        indentLevel++;

        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                writer.write("\n");
            }

            writeIndentation();
            writer.write("- ");

            ONode item = list.get(i);
            if (item.isObject() || item.isArray()) {
                if (!item.isArray() || !item.getArray().isEmpty()) {
                    writer.write("\n");
                }
                writeNode(item, true, false);
            } else {
                writeNode(item, true, false);
            }
        }

        indentLevel--;
    }

    private void writeString(String s, boolean isSequenceItem, boolean isMapValue) throws IOException {
        if (s == null) {
            writeNull(isSequenceItem, isMapValue);
            return;
        }

        if (s.isEmpty()) {
            writer.write("\"\"");
            return;
        }

        if (isMultiLineString(s)) {
            writeBlockString(s);
        } else if (needsQuotes(s)) {
            writeQuotedString(s, '"');
        } else {
            writer.write(s);
        }
    }

    private void writeNumber(Number num, boolean isSequenceItem, boolean isMapValue) throws IOException {
        if (num == null) {
            writeNull(isSequenceItem, isMapValue);
            return;
        }

        if (opts.hasFeature(Feature.Write_NumbersAsString)) {
            writeQuotedString(num.toString(), '"');
        } else if (opts.hasFeature(Feature.Write_BigNumbersAsString) && Asserts.isBigNumber(num)) {
            writeQuotedString(num.toString(), '"');
        } else if (opts.hasFeature(Feature.Write_LongAsString) && num instanceof Long) {
            writeQuotedString(num.toString(), '"');
        } else {
            writer.write(num.toString());
        }
    }

    private void writeDate(java.util.Date date, boolean isSequenceItem, boolean isMapValue) throws IOException {
        if (date == null) {
            writeNull(isSequenceItem, isMapValue);
            return;
        }

        if (opts.hasFeature(Feature.Write_UseDateFormat)) {
            String dateStr = DateUtil.format(date, opts.getDateFormat());
            writeString(dateStr, isSequenceItem, isMapValue);
        } else {
            writeNumber(date.getTime(), isSequenceItem, isMapValue);
        }
    }

    private void writeBoolean(boolean value, boolean isSequenceItem, boolean isMapValue) throws IOException {
        writer.write(value ? "true" : "false");
    }

    private void writeNull(boolean isSequenceItem, boolean isMapValue) throws IOException {
        writer.write("null");
    }

    private void writeKey(String key, boolean inFlow) throws IOException {
        if (key == null || key.isEmpty()) {
            writer.write("\"\"");
            return;
        }

        if (inFlow || needsQuotes(key)) {
            writeQuotedString(key, '"');
        } else {
            // 转换蛇形命名
            if (opts.hasFeature(Feature.Write_UseSnakeStyle)) {
                key = toSnakeStyle(key);
            }
            writer.write(key);
        }
    }

    private void writeQuotedString(String s, char quoteChar) throws IOException {
        writer.write(quoteChar);
        writeEscapedContent(s, quoteChar);
        writer.write(quoteChar);
    }

    private void writeBlockString(String s) throws IOException {
        String[] lines = s.split("\n", -1);

        if (lines.length <= 1) {
            // 单行字符串，使用普通格式
            writeString(s, false, false);
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

        indentLevel++;
        for (String line : lines) {
            writeIndentation();
            writer.write(line);
            writer.write("\n");
        }
        indentLevel--;
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

    private void writeIndentation() throws IOException {
        for (int i = 0; i < indentLevel; i++) {
            writer.write(indent);
        }
    }

    private boolean shouldUseFlowStyle() {
        // 简化逻辑：只在顶层且小规模时使用流式风格
        return indentLevel == 0;
    }

    private boolean needsQuotes(String s) {
        if (s == null || s.isEmpty()) {
            return true;
        }

        // 检查首字符
        char firstChar = s.charAt(0);
        if ("-?:[]{}#&*!|>\"'%@`".indexOf(firstChar) >= 0 || Character.isWhitespace(firstChar)) {
            return true;
        }

        // 检查尾字符
        char lastChar = s.charAt(s.length() - 1);
        if (Character.isWhitespace(lastChar)) {
            return true;
        }

        // 检查是否包含特殊字符
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (":[]{}#,".indexOf(c) >= 0 || Character.isWhitespace(c) || c < 0x20) {
                return true;
            }
        }

        // 检查是否为可能被误解的值
        return isAmbiguousValue(s);
    }

    private boolean isAmbiguousValue(String s) {
        if (s == null) return false;

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

    private boolean isMultiLineString(String s) {
        return s != null && s.contains("\n") && s.length() > 20;
    }

    private String toSnakeStyle(String camelName) {
        if (camelName == null || camelName.isEmpty()) {
            return camelName;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camelName.length(); i++) {
            char c = camelName.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}