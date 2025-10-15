package org.noear.snack4.json;

import org.noear.snack4.ONode;
import org.noear.snack4.node.Options;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 *
 * @author noear 2025/10/15 created
 *
 */
public class JsonProviderImpl implements JsonProvider {
    @Override
    public ONode read(Reader reader, Options opts) throws IOException {
        return JsonReader.read(reader, opts);
    }

    @Override
    public void write(ONode node, Options opts, Writer writer) throws IOException {
        JsonWriter.write(node, opts, writer);
    }

    @Override
    public String warnHint() {
        return null;
    }
}