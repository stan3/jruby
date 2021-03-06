/*
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.jruby.truffle.nodes.literal.array;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import org.jruby.truffle.nodes.*;
import org.jruby.truffle.runtime.*;
import org.jruby.truffle.runtime.core.RubyArray;

public abstract class ArrayLiteralNode extends RubyNode {

    @Children protected final RubyNode[] values;

    public ArrayLiteralNode(RubyContext context, SourceSection sourceSection, RubyNode[] values) {
        super(context, sourceSection);
        this.values = values;
    }

    protected RubyArray makeGeneric(VirtualFrame frame, Object[] alreadyExecuted) {
        CompilerAsserts.neverPartOfCompilation();

        replace(new ObjectArrayLiteralNode(getContext(), getSourceSection(), values));

        final Object[] executedValues = new Object[values.length];

        for (int n = 0; n < values.length; n++) {
            if (n < alreadyExecuted.length) {
                executedValues[n] = alreadyExecuted[n];
            } else {
                executedValues[n] = values[n].execute(frame);
            }
        }

        return RubyArray.fromObjects(getContext().getCoreLibrary().getArrayClass(), executedValues);
    }

    @Override
    public abstract RubyArray executeArray(VirtualFrame frame);

    @ExplodeLoop
    @Override
    public void executeVoid(VirtualFrame frame) {
        for (int n = 0; n < values.length; n++) {
            values[n].executeVoid(frame);
        }
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return executeArray(frame);
    }

    @Override
    public Object isDefined(VirtualFrame frame) {
        return getContext().makeString("expression");
    }

    // TODO(CS): remove this - shouldn't be fiddling with nodes from the outside
    public RubyNode[] getValues() {
        return values;
    }

}
