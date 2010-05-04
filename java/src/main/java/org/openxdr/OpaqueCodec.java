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

import static org.openxdr.Utility.decodeAlign;
import static org.openxdr.Utility.encodeAlign;

import java.nio.ByteBuffer;

final class OpaqueCodec implements Codec<OpaqueSlice> {
    private final int size;

    OpaqueCodec(int size) {
        this.size = size;
    }

    static void encodeOpaque(ByteBuffer buf, byte[] val, int offset, int len) {
        encodeAlign(buf, val, offset, len);
    }

    static void encodeOpaque(ByteBuffer buf, byte[] val) {
        encodeOpaque(buf, val, 0, val.length);
    }

    static void decodeOpaque(ByteBuffer buf, byte[] val, int offset, int len) {
        decodeAlign(buf, val, offset, len);
    }

    static void decodeOpaque(ByteBuffer buf, byte[] val) {
        decodeOpaque(buf, val, 0, val.length);
    }

    public final void encode(ByteBuffer buf, OpaqueSlice val) {
        if (val.getLength() != size)
            throw new IllegalArgumentException();
        encodeOpaque(buf, val.getBuffer(), val.getOffset(), val.getLength());
    }

    public final OpaqueSlice decode(ByteBuffer buf) {
        final OpaqueSlice val = new OpaqueSlice(size);
        decodeOpaque(buf, val.getBuffer(), val.getOffset(), val.getLength());
        return val;
    }
}
