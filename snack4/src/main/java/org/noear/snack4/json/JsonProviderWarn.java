package org.noear.snack4.json;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 *
 * @author noear 2025/10/15 created
 *
 */
public class JsonProviderWarn implements JsonProvider {
    @Override
    public ONode read(Reader reader, Options opts) throws IOException {
        throw new UnsupportedOperationException("Requires 'snack4-json' dependency");
    }

    @Override
    public void write(ONode node, Options opts, Writer writer) throws IOException {
        throw new UnsupportedOperationException("Requires 'snack4-json' dependency");
    }
}