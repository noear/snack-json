package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;

import java.net.InetSocketAddress;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class InetSocketAddressDecoder implements ObjectDecoder<InetSocketAddress> {
    @Override
    public InetSocketAddress decode(DecodeContext ctx, ONode node) {
        String hostname = node.get("hostname").getString();
        int port = node.get("port").getInt();
        return new InetSocketAddress(hostname, port);
    }
}
