/*
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.jruby.truffle.nodes.cast;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;
import org.jruby.truffle.nodes.*;
import org.jruby.truffle.nodes.call.DispatchHeadNode;
import org.jruby.truffle.runtime.*;
import org.jruby.truffle.runtime.control.RaiseException;
import org.jruby.truffle.runtime.core.RubyArray;

@NodeInfo(shortName = "array-cast")
@NodeChild("child")
public abstract class ArrayCastNode extends RubyNode {

    @Child protected DispatchHeadNode toArrayNode;

    public ArrayCastNode(RubyContext context, SourceSection sourceSection) {
        super(context, sourceSection);
        toArrayNode = new DispatchHeadNode(context, "to_ary", false, DispatchHeadNode.MissingBehavior.RETURN_MISSING);
    }

    public ArrayCastNode(ArrayCastNode prev) {
        super(prev);
        toArrayNode = prev.toArrayNode;
    }

    protected abstract RubyNode getChild();

    @Specialization
    public RubyArray doArray(RubyArray array) {
        return array;
    }

    @Specialization
    public NilPlaceholder doNil(NilPlaceholder nilPlaceholder) {
        return nilPlaceholder;
    }

    @Specialization
    public Object doObject(VirtualFrame frame, Object object) {
        notDesignedForCompilation();

        final Object result = toArrayNode.dispatch(frame, object, null, new Object[]{});

        if (result == DispatchHeadNode.MISSING) {
            return NilPlaceholder.INSTANCE;
        }

        if (!(result instanceof RubyArray)) {
            CompilerDirectives.transferToInterpreter();
            throw new RaiseException(getContext().getCoreLibrary().typeErrorShouldReturn(object.toString(), toArrayNode.getName(), "Array"));
        }

        return result;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        getChild().executeVoid(frame);
    }

}
