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
import java.nio.charset.CharacterCodingException;

public final class XdrOpaque {

    private XdrOpaque() {
    }

    public static void encode(ByteBuffer buf, byte[] val, int offset, int len) {
        encodeAlign(buf, val, offset, len);
    }

    public static void encode(ByteBuffer buf, byte[] val) {
        encode(buf, val, 0, val.length);
    }

    public static void decode(ByteBuffer buf, byte[] val, int offset, int len) {
        decodeAlign(buf, val, offset, len);
    }

    public static void decode(ByteBuffer buf, byte[] val) {
        decode(buf, val, 0, val.length);
    }

    public static Codec<OpaqueSlice> newCodec(final int size) {
        return new Codec<OpaqueSlice>() {
            public final void encode(ByteBuffer buf, OpaqueSlice val) {
                if (val.getLength() != size)
                    throw new IllegalArgumentException();
                XdrOpaque.encode(buf, val.getBuffer(), val.getOffset(), val
                        .getLength());
            }

            public final OpaqueSlice decode(ByteBuffer buf) {
                final OpaqueSlice val = new OpaqueSlice(size);
                XdrOpaque.decode(buf, val.getBuffer(), val.getOffset(), val
                        .getLength());
                return val;
            }
        };
    }

    public static void encodeVar(ByteBuffer buf, byte[] val, int offset,
            int len, int maxsize) {
        if (maxsize < len)
            throw new IllegalArgumentException();
        XdrInt.encode(buf, len);
        XdrOpaque.encode(buf, val, offset, len);
    }

    public static void encodeVar(ByteBuffer buf, byte[] val, int maxsize) {
        encodeVar(buf, val, 0, val.length, maxsize);
    }

    public static void encodeVar(ByteBuffer buf, byte[] val) {
        encodeVar(buf, val, Integer.MAX_VALUE);
    }

    public static byte[] decodeVar(ByteBuffer buf, int maxsize) {
        final int len = XdrInt.decode(buf);
        if (maxsize < len)
            throw new IllegalArgumentException();
        final byte[] dst = new byte[len];
        XdrOpaque.decode(buf, dst, 0, dst.length);
        return dst;
    }

    public static byte[] decodeVar(ByteBuffer buf) {
        return decodeVar(buf, Integer.MAX_VALUE);
    }

    public static Codec<OpaqueSlice> newVarCodec(final int maxsize) {
        return new Codec<OpaqueSlice>() {
            public final void encode(ByteBuffer buf, OpaqueSlice val)
                    throws CharacterCodingException {
                XdrOpaque.encodeVar(buf, val.getBuffer(), val.getOffset(), val
                        .getLength(), maxsize);
            }

            public final OpaqueSlice decode(ByteBuffer buf)
                    throws CharacterCodingException {
                return new OpaqueSlice(XdrOpaque.decodeVar(buf, maxsize));
            }
        };
    }

    public static final Codec<OpaqueSlice> VAR_CODEC = newVarCodec(Integer.MAX_VALUE);
}
