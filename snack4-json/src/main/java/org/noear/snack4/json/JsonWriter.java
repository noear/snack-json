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
package org.noear.snack4.json;

import org.noear.snack4.node.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.node.Options;
import org.noear.snack4.codec.util.DateUtil;
import org.noear.snack4.json.util.IoUtil;
import org.noear.snack4.util.Asserts;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Json 书写器
 *
 * @author noear 2025/3/16 created
 * @since 4.0
 */
public class JsonWriter {
    public static String write(ONode node, Options opts) throws IOException {
        StringWriter writer = new StringWriter();
        write(node, opts, writer);
        return writer.toString();
    }

    public static void write(ONode node, Options opts, Writer writer) throws IOException {
        new JsonWriter(opts, writer).write(node);
    }

    /// ////////////

    private final Options opts;
    private final Writer writer;
    private int depth = 0;
    private StringBuilder stringBuilder;

    private StringBuilder getStringBuilder() {
        if (stringBuilder == null) {
            stringBuilder = new StringBuilder(32);
        } else {
            stringBuilder.setLength(0);
        }
        return stringBuilder;
    }

    public JsonWriter(Options opts, Writer writer) {
        Objects.requireNonNull(writer, "writer");

        this.writer = writer;
        this.opts = opts == null ? Options.DEF_OPTIONS : opts;
    }

    public void write(ONode node) throws IOException {
        switch (node.getType()) {
            case Object:
                writeObject(node.getObject());
                break;
            case Array:
                writeArray(node.getArray());
                break;
            case String:
                writeString(node.getString());
                break;
            case Number:
                if (opts.hasFeature(Feature.Write_NumbersAsString)) {
                    writeString(String.valueOf(node.getValue()));
                } else {
                    writeNumber(node.getNumber());
                }
                break;
            case Date:
                if (opts.hasFeature(Feature.Write_UseDateFormat)) {
                    writeString(DateUtil.format(node.getDate(), opts.getDateFormat()));
                } else {
                    writeNumber(node.getDate().getTime());
                }
                break;
            case Boolean:
                writer.write(node.getBoolean() ? "true" : "false");
                break;
            case Null:
            case Undefined:
                writer.write("null");
                break;
        }
    }

    private void writeObject(Map<String, ONode> map) throws IOException {
        writer.write('{');
        depth++;
        boolean first = true;
        for (Map.Entry<String, ONode> entry : map.entrySet()) {
            if (entry.getValue().isNull()) {
                if (opts.hasFeature(Feature.Write_Nulls) == false) {
                    continue;
                }
            }

            if (!first) {
                writer.write(',');
            }
            writeIndentation();

            String key = opts.hasFeature(Feature.Write_UseSnakeStyle) ?
                    toSnakeStyle(entry.getKey()) : entry.getKey();
            writeKey(key);
            writer.write(':');
            if (opts.hasFeature(Feature.Write_PrettyFormat)) {
                writer.write(' ');
            }
            write(entry.getValue());
            first = false;
        }
        depth--;
        writeIndentation();
        writer.write('}');
    }

    private void writeArray(List<ONode> list) throws IOException {
        writer.write('[');
        depth++;
        boolean first = true;
        for (ONode item : list) {
            if (!first) {
                writer.write(',');
            }
            writeIndentation();
            write(item);
            first = false;
        }
        depth--;
        writeIndentation();
        writer.write(']');
    }

    private void writeIndentation() throws IOException {
        if (opts.hasFeature(Feature.Write_PrettyFormat)) {
            writer.write('\n');
            for (int i = 0; i < depth; i++) {
                writer.write(opts.getWriteIndent());
            }
        }
    }

    private void writeNumber(Number num) throws IOException {
        if (opts.hasFeature(Feature.Write_BigNumbersAsString) && Asserts.isBigNumber(num)) {
            writer.write('"' + num.toString() + '"');
            return;
        }

        if (opts.hasFeature(Feature.Write_LongAsString) && num instanceof Long) {
            writer.write('"' + num.toString() + '"');
            return;

        }

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

    private void writeKey(String s) throws IOException {
        if (opts.hasFeature(Feature.Write_UnquotedFieldNames)) {
            writeEscapeString(s, '"', opts);
        } else {
            writeString(s);
        }
    }

    private void writeString(String s) throws IOException {
        char quoteChar = opts.hasFeature(Feature.Write_UseSingleQuotes) ? '\'' : '"';
        writer.write(quoteChar);
        writeEscapeString(s, quoteChar, opts);
        writer.write(quoteChar);
    }

    private void writeEscapeString(String s, char quoteChar, Options opts) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c >= '\0' && c <= '\7') {
                writer.write("\\u000");
                writer.write(IoUtil.CHARS_MARK[(int) c]);
                continue;
            }

            if (c == quoteChar || c == '\n' || c == '\r' || c == '\t' || c == '\f' || c == '\b') {
                writer.write('\\');
                writer.write(IoUtil.CHARS_MARK[(int) c]);
                continue;
            }

            if (c == '\\') {
                if (opts.hasFeature(Feature.Write_UseRawBackslash)) {
                    writer.write('\\');
                } else {
                    writer.write("\\\\");
                }
                continue;
            }

            if (c < 0x20 || (opts.hasFeature(Feature.Write_BrowserCompatible) && c > 0x7F)) {
                writer.write(String.format("\\u%04x", (int) c));
            } else {
                writer.write(c);
            }
        }
    }

    private String toSnakeStyle(String camelName) {
        StringBuilder sb = getStringBuilder();
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