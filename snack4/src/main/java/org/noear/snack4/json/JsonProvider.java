package org.noear.snack4.json;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;

import java.io.*;

/**
 *
 * @author noear 2025/10/15 created
 *
 */
public interface JsonProvider {
    ONode read(Reader reader, Options opts) throws IOException;

    void write(ONode node, Options opts, Writer writer) throws IOException;

    default ONode read(String json, Options opts) throws IOException {
        return read(new StringReader(json), opts);
    }

    default String write(ONode node, Options opts) throws IOException {
        StringWriter writer = new StringWriter();
        write(node, opts, writer);
        return writer.toString();
    }
}