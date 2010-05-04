/**
 * Copyright 2010 Mark Aylett <mark.aylett@gmail.com>
 * 
 * The contents of this file are subject to the Common Development and
 * Distribution License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.sun.com/cddl/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 */
package org.openxdr;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class XdrBuffer {
    private XdrBuffer() {
    }

    public static ByteBuffer allocateDirect(int capacity) {
        final ByteBuffer buf = ByteBuffer.allocateDirect(capacity);
        buf.order(ByteOrder.BIG_ENDIAN);
        return buf;
    }

    public static ByteBuffer allocate(int capacity) {
        final ByteBuffer buf = ByteBuffer.allocate(capacity);
        buf.order(ByteOrder.BIG_ENDIAN);
        return buf;
    }

    public static ByteBuffer wrap(byte[] array, int offset, int length) {
        final ByteBuffer buf = ByteBuffer.wrap(array, offset, length);
        buf.order(ByteOrder.BIG_ENDIAN);
        return buf;
    }

    public static ByteBuffer wrap(byte[] array) {
        final ByteBuffer buf = ByteBuffer.wrap(array);
        buf.order(ByteOrder.BIG_ENDIAN);
        return buf;
    }
}
