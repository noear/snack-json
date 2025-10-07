package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class InetSocketAddressEncoder implements ObjectEncoder<InetSocketAddress> {
    @Override
    public ONode encode(EncodeContext ctx, InetSocketAddress value) {
        InetAddress inetAddress = value.getAddress();

        ONode node = new ONode();
        node.set("hostname", inetAddress.getHostAddress());
        node.set("port", value.getPort());

        return node;
    }
}
