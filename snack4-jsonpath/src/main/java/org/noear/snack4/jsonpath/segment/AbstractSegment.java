package org.noear.snack4.jsonpath.segment;

import org.noear.snack4.jsonpath.Segment;

public abstract class AbstractSegment implements Segment {
    private boolean flattened;

    public boolean isFlattened() {
        return flattened;
    }

    @Override
    public void before(Segment segment) {
        flattened = segment instanceof DescendantSegment;
    }
}
