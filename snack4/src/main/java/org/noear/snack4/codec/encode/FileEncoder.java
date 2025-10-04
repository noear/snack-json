package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.NodeEncoder;

import java.io.File;

/**
 *
 * @author noear 2025/10/3 created
 */
public class FileEncoder implements NodeEncoder<File> {
    @Override
    public ONode encode(Options opts, ONodeAttr attr, File value) {
        return new ONode(value.getPath());
    }
}