package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.NodeEncoder;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class InetSocketAddressEncoder implements NodeEncoder<InetSocketAddress> {
    @Override
    public ONode encode(Options opts, ONodeAttr attr, InetSocketAddress value) {
        InetAddress inetAddress = value.getAddress();

        ONode node = new ONode();
        node.set("hostname", inetAddress.getHostAddress());
        node.set("port", value.getPort());

        return node;
    }
}
