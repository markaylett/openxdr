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

import static org.openxdr.IntCodec.decodeInt;
import static org.openxdr.IntCodec.encodeInt;
import static org.openxdr.OpaqueCodec.decodeOpaque;
import static org.openxdr.OpaqueCodec.encodeOpaque;

import java.nio.ByteBuffer;

public final class VarOpaqueCodec implements Codec<OpaqueSlice> {
    private static final VarOpaqueCodec instance = new VarOpaqueCodec();
    private final int maxsize;

    private VarOpaqueCodec() {
        this(Integer.MAX_VALUE);
    }

    public VarOpaqueCodec(int maxsize) {
        this.maxsize = maxsize;

    }

    public final void encode(ByteBuffer buf, OpaqueSlice val) {
        encodeVarOpaque(buf, val.getBuffer(), val.getOffset(), val.getLength(),
                maxsize);
    }

    public final OpaqueSlice decode(ByteBuffer buf) {
        return new OpaqueSlice(decodeVarOpaque(buf, maxsize));
    }

    public static VarOpaqueCodec getInstance() {
        return instance;
    }

    public static void encodeVarOpaque(ByteBuffer buf, byte[] val, int offset,
            int len, int maxsize) {
        if (maxsize < len)
            throw new IllegalArgumentException();
        encodeInt(buf, len);
        encodeOpaque(buf, val, offset, len);
    }

    public static void encodeVarOpaque(ByteBuffer buf, byte[] val, int maxsize) {
        encodeVarOpaque(buf, val, 0, val.length, maxsize);
    }

    public static void encodeVarOpaque(ByteBuffer buf, byte[] val) {
        encodeVarOpaque(buf, val, Integer.MAX_VALUE);
    }

    public static byte[] decodeVarOpaque(ByteBuffer buf, int maxsize) {
        final int len = decodeInt(buf);
        if (maxsize < len)
            throw new IllegalArgumentException();
        final byte[] dst = new byte[len];
        decodeOpaque(buf, dst, 0, dst.length);
        return dst;
    }

    public static byte[] decodeVarOpaque(ByteBuffer buf) {
        return decodeVarOpaque(buf, Integer.MAX_VALUE);
    }
}