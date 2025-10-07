package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;

import java.io.File;

/**
 *
 * @author noear 2025/10/3 created
 */
public class FileEncoder implements ObjectEncoder<File> {
    @Override
    public ONode encode(EncodeContext ctx, File value) {
        return new ONode(value.getPath());
    }
}