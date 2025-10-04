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

import org.noear.snack4.ONode;
import org.noear.snack4.Feature;
import org.noear.snack4.Options;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class JsonWriter {
    public static String serialize(ONode node, Options opts) throws IOException {
        StringWriter writer = new StringWriter();
        new JsonWriter(opts, writer).write(node);
        return writer.toString();
    }

    final Options opts;
    final Writer writer;
    int depth = 0;

    public JsonWriter(Options opts, Writer writer) {
        this.opts = opts != null ? opts : Options.def();
        this.writer = writer;
    }

    public void write(ONode node) throws IOException {
        switch (node.getType()) {
            case TYPE_OBJECT:
                writeObject(node.getObject());
                break;
            case TYPE_ARRAY:
                writeArray(node.getArray());
                break;
            case TYPE_STRING:
                writeString(node.getString());
                break;
            case TYPE_NUMBER:
                if(opts.isFeatureEnabled(Feature.Write_UseBigNumberMode)) {
                    writeString(String.valueOf(node.getValue()));
                }else {
                    writeNumber(node.getNumber());
                }
                break;
            case TYPE_BOOLEAN:
                writer.write(node.getBoolean() ? "true" : "false");
                break;
            case TYPE_NULL:
                writer.write("null");
                break;
        }
    }

    private void writeObject(Map<String, ONode> map) throws IOException {
        writer.write('{');
        depth++;
        boolean first = true;
        for (Map.Entry<String, ONode> entry : map.entrySet()) {
            if (opts.isFeatureEnabled(Feature.Write_SkipNullValue) && entry.getValue().isNull()) {
                continue;
            }

            if (!first) {
                writer.write(',');
            }
            writeIndentation();

            String key = opts.isFeatureEnabled(Feature.Write_UseUnderlineStyle) ?
                    toUnderlineName(entry.getKey()) : entry.getKey();
            writeString(key);
            writer.write(':');
            if (opts.isFeatureEnabled(Feature.Write_PrettyFormat)) {
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
        if (opts.isFeatureEnabled(Feature.Write_PrettyFormat)) {
            writer.write('\n');
            for (int i = 0; i < depth; i++) {
                writer.write(opts.getIndent());
            }
        }
    }

    private void writeNumber(Number num) throws IOException {
        if (opts.isFeatureEnabled(Feature.Write_UseBigNumberMode) && num instanceof Double) {
            writer.write('"' + num.toString() + '"');
        } else {
            writer.write(num.toString());
        }
    }

    private void writeString(String s) throws IOException {
        char quoteChar = opts.isFeatureEnabled(Feature.Write_UseSingleQuotes) ? '\'' : '"';
        writer.write(quoteChar);
        writer.write(escapeString(s, opts));
        writer.write(quoteChar);
    }

    private static String escapeString(String s, Options opts) {
        StringBuilder sb = new StringBuilder(s.length() * 2);
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\'':
                    sb.append("\\'");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20 || (opts.isFeatureEnabled(Feature.Write_EscapeNonAscii) && c > 0x7F)) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    private String toUnderlineName(String camelName) {
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