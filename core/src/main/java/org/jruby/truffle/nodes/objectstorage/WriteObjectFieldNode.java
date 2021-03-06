/*
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.jruby.truffle.nodes.objectstorage;

import com.oracle.truffle.api.nodes.Node;
import org.jruby.truffle.runtime.objectstorage.*;

public abstract class WriteObjectFieldNode extends Node {

    public abstract void execute(ObjectStorage object, Object value);

    public void execute(ObjectStorage object, int value) {
        execute(object, (Object) value);
    }

    public void execute(ObjectStorage object, long value) {
        execute(object, (Object) value);
    }

    public void execute(ObjectStorage object, double value) {
        execute(object, (Object) value);
    }

}
