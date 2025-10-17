package org.noear.snack4.jsonpath.selector;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.QueryContext;

import java.util.function.Consumer;

/**
 *
 * @author noear 2025/10/17 created
 *
 */
public abstract class AbstractSelector implements Selector {
    private AbstractSelector next;

    protected void onComplete(QueryContext ctx, ONode node, Consumer<ONode> acceptor) {
        if (next != null) {
            next.onNext(ctx, node, acceptor);
        } else {
            acceptor.accept(node);
        }
    }
}