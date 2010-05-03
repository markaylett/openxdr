/**
 * Copyright 2010 Mark Aylett <mark.aylett@gmail.com>
 *
 * The contents of this file are subject to the Common Development and
 * Distribution License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.sun.com/cddl/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 */
package org.openxdr;

import static org.openxdr.ArrayCodec.decodeArray;
import static org.openxdr.ArrayCodec.encodeArray;
import static org.openxdr.IntCodec.decodeInt;
import static org.openxdr.IntCodec.encodeInt;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

public class VarArrayCodec<T> implements Codec<ArraySlice<T>> {
    private final Codec<T> codec;
    private final int maxsize;

    public VarArrayCodec(Codec<T> codec, int maxsize) {
        this.codec = codec;
        this.maxsize = maxsize;
    }

    public VarArrayCodec(Codec<T> codec) {
        this(codec, Integer.MAX_VALUE);
    }

    public final void encode(ByteBuffer buf, ArraySlice<T> val)
            throws CharacterCodingException {
        encodeVarArray(buf, val.getBuffer(), val.getOffset(), val.getLength(),
                codec, maxsize);
    }

    public final ArraySlice<T> decode(ByteBuffer buf)
            throws CharacterCodingException {
        return new ArraySlice<T>(decodeVarArray(buf, codec, maxsize));
    }

    public static <T> void encodeVarArray(ByteBuffer buf, T[] val, int offset,
            int len, Codec<T> codec, int maxsize)
            throws CharacterCodingException {
        if (maxsize < len)
            throw new IllegalArgumentException();
        encodeInt(buf, len);
        encodeArray(buf, val, offset, len, codec);
    }

    public static <T> void encodeVarArray(ByteBuffer buf, T[] val, int offset,
            int len, Codec<T> codec) throws CharacterCodingException {
        encodeVarArray(buf, val, offset, len, codec, Integer.MAX_VALUE);
    }

    public static <T> void encodeVarArray(ByteBuffer buf, T[] val,
            Codec<T> codec, int maxsize) throws CharacterCodingException {
        encodeVarArray(buf, val, 0, val.length, codec, maxsize);
    }

    public static <T> void encodeVarArray(ByteBuffer buf, T[] val,
            Codec<T> codec) throws CharacterCodingException {
        encodeVarArray(buf, val, codec, Integer.MAX_VALUE);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] decodeVarArray(ByteBuffer buf, Codec<T> codec,
            int maxsize) throws CharacterCodingException {
        final int len = decodeInt(buf);
        if (maxsize < len)
            throw new IllegalArgumentException();
        final T[] dst = (T[]) new Object[len];
        decodeArray(buf, dst, 0, dst.length, codec);
        return dst;
    }

    public static <T> T[] decodeVarArray(ByteBuffer buf, Codec<T> codec)
            throws CharacterCodingException {
        return decodeVarArray(buf, codec, Integer.MAX_VALUE);
    }
}