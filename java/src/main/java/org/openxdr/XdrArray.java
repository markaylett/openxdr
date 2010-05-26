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
import java.nio.charset.CharacterCodingException;

public final class XdrArray {

    private XdrArray() {
    }

    public static <T> void encode(ByteBuffer buf, T[] val, int offset, int len,
            Codec<T> codec) throws CharacterCodingException {
        final int j = offset + len;
        for (int i = offset; i < j; ++i)
            codec.encode(buf, val[i]);
    }

    public static <T> void encode(ByteBuffer buf, T[] val, Codec<T> codec)
            throws CharacterCodingException {
        encode(buf, val, 0, val.length, codec);
    }

    public static <T> void decode(ByteBuffer buf, T[] val, int offset, int len,
            Codec<T> codec) throws CharacterCodingException {
        final int j = offset + len;
        for (int i = offset; i < j; ++i)
            val[i] = codec.decode(buf);
    }

    public static <T> void decode(ByteBuffer buf, T[] val, Codec<T> codec)
            throws CharacterCodingException {
        decode(buf, val, 0, val.length, codec);
    }

    public static <T> int size(T[] val, int offset, int len, Codec<T> codec) {
        int n = 0;
        final int j = offset + len;
        for (int i = offset; i < j; ++i)
            n += codec.size(val[i]);
        return n;
    }

    public static <T> int size(T[] val, Codec<T> codec) {
        return size(val, 0, val.length, codec);
    }

    public static <T> Codec<Array<T>> newCodec(final Codec<T> codec,
            final int size) {
        return new Codec<Array<T>>() {
            public final void encode(ByteBuffer buf, Array<T> val)
                    throws CharacterCodingException {
                if (val.getLength() != size)
                    throw new IllegalArgumentException();
                XdrArray.encode(buf, val.getBuffer(), val.getOffset(), val
                        .getLength(), codec);
            }

            public final Array<T> decode(ByteBuffer buf)
                    throws CharacterCodingException {
                final Array<T> val = new Array<T>(size);
                XdrArray.decode(buf, val.getBuffer(), val.getOffset(), val
                        .getLength(), codec);
                return val;
            }

            public final int size(Array<T> val) {
                if (val.getLength() != size)
                    throw new IllegalArgumentException();
                return XdrArray.size(val.getBuffer(), val.getOffset(), val
                        .getLength(), codec);
            }
        };
    }

    public static <T> void encodeVar(ByteBuffer buf, T[] val, int offset,
            int len, Codec<T> codec, int maxsize)
            throws CharacterCodingException {
        if (maxsize < len)
            throw new IllegalArgumentException();
        XdrInt.encode(buf, len);
        XdrArray.encode(buf, val, offset, len, codec);
    }

    public static <T> void encodeVar(ByteBuffer buf, T[] val, int offset,
            int len, Codec<T> codec) throws CharacterCodingException {
        encodeVar(buf, val, offset, len, codec, Integer.MAX_VALUE);
    }

    public static <T> void encodeVar(ByteBuffer buf, T[] val, Codec<T> codec,
            int maxsize) throws CharacterCodingException {
        encodeVar(buf, val, 0, val.length, codec, maxsize);
    }

    public static <T> void encodeVar(ByteBuffer buf, T[] val, Codec<T> codec)
            throws CharacterCodingException {
        encodeVar(buf, val, codec, Integer.MAX_VALUE);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] decodeVar(ByteBuffer buf, Codec<T> codec, int maxsize)
            throws CharacterCodingException {
        final int len = XdrInt.decode(buf);
        if (maxsize < len)
            throw new IllegalArgumentException();
        final T[] dst = (T[]) new Object[len];
        XdrArray.decode(buf, dst, 0, dst.length, codec);
        return dst;
    }

    public static <T> T[] decodeVar(ByteBuffer buf, Codec<T> codec)
            throws CharacterCodingException {
        return decodeVar(buf, codec, Integer.MAX_VALUE);
    }

    public static <T> int sizeVar(T[] val, int offset, int len, Codec<T> codec,
            int maxsize) {
        if (maxsize < len)
            throw new IllegalArgumentException();
        return XdrInt.SIZE + XdrArray.size(val, offset, len, codec);
    }

    public static <T> int sizeVar(T[] val, int offset, int len, Codec<T> codec) {
        return sizeVar(val, offset, len, codec, Integer.MAX_VALUE);
    }

    public static <T> int sizeVar(T[] val, Codec<T> codec, int maxsize) {
        return sizeVar(val, 0, val.length, codec, maxsize);
    }

    public static <T> int sizeVar(T[] val, Codec<T> codec) {
        return sizeVar(val, codec, Integer.MAX_VALUE);
    }

    public static <T> Codec<Array<T>> newVarCodec(final Codec<T> codec,
            final int maxsize) {
        return new Codec<Array<T>>() {
            public final void encode(ByteBuffer buf, Array<T> val)
                    throws CharacterCodingException {
                XdrArray.encodeVar(buf, val.getBuffer(), val.getOffset(), val
                        .getLength(), codec, maxsize);
            }

            public final Array<T> decode(ByteBuffer buf)
                    throws CharacterCodingException {
                return new Array<T>(XdrArray.decodeVar(buf, codec, maxsize));
            }

            public final int size(Array<T> val) {
                return XdrArray.sizeVar(val.getBuffer(), val.getOffset(), val
                        .getLength(), codec, maxsize);
            }
        };
    }

    public static <T> Codec<Array<T>> newVarCodec(final Codec<T> codec) {
        return newVarCodec(codec, Integer.MAX_VALUE);
    }
}
