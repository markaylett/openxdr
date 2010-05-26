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

import static org.openxdr.Utility.aligned;
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

    public static int size(byte[] val, int offset, int len) {
        return aligned(len);
    }

    public static int size(byte[] val) {
        return size(val, 0, val.length);
    }

    public static Codec<Opaque> newCodec(final int size) {
        return new Codec<Opaque>() {
            public final void encode(ByteBuffer buf, Opaque val) {
                if (val.getLength() != size)
                    throw new IllegalArgumentException();
                XdrOpaque.encode(buf, val.getBuffer(), val.getOffset(), val
                        .getLength());
            }

            public final Opaque decode(ByteBuffer buf) {
                final Opaque val = new Opaque(size);
                XdrOpaque.decode(buf, val.getBuffer(), val.getOffset(), val
                        .getLength());
                return val;
            }

            public final int size(Opaque val) {
                if (val.getLength() != size)
                    throw new IllegalArgumentException();
                return XdrOpaque.size(val.getBuffer(), val.getOffset(), val
                        .getLength());
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

    public static int sizeVar(byte[] val, int offset, int len, int maxsize) {
        if (maxsize < len)
            throw new IllegalArgumentException();
        return XdrInt.SIZE + aligned(len);
    }

    public static int sizeVar(byte[] val, int maxsize) {
        return sizeVar(val, 0, val.length, maxsize);
    }

    public static int sizeVar(byte[] val) {
        return sizeVar(val, Integer.MAX_VALUE);
    }

    public static Codec<Opaque> newVarCodec(final int maxsize) {
        return new Codec<Opaque>() {
            public final void encode(ByteBuffer buf, Opaque val)
                    throws CharacterCodingException {
                XdrOpaque.encodeVar(buf, val.getBuffer(), val.getOffset(), val
                        .getLength(), maxsize);
            }

            public final Opaque decode(ByteBuffer buf)
                    throws CharacterCodingException {
                return new Opaque(XdrOpaque.decodeVar(buf, maxsize));
            }

            public final int size(Opaque val) {
                return XdrOpaque.sizeVar(val.getBuffer(), val.getOffset(), val
                        .getLength(), maxsize);
            }
        };
    }

    public static final Codec<Opaque> VAR_CODEC = newVarCodec(Integer.MAX_VALUE);
}
