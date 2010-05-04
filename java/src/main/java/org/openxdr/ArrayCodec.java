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

final class ArrayCodec<T> implements Codec<ArraySlice<T>> {
    private final Codec<T> codec;
    private final int size;

    ArrayCodec(Codec<T> codec, int size) {
        this.codec = codec;
        this.size = size;
    }

    static <T> void encodeArray(ByteBuffer buf, T[] val, int offset, int len,
            Codec<T> codec) throws CharacterCodingException {
        final int j = offset + len;
        for (int i = offset; i < j; ++i)
            codec.encode(buf, val[i]);
    }

    static <T> void encodeArray(ByteBuffer buf, T[] val, Codec<T> codec)
            throws CharacterCodingException {
        encodeArray(buf, val, 0, val.length, codec);
    }

    static <T> void decodeArray(ByteBuffer buf, T[] val, int offset, int len,
            Codec<T> codec) throws CharacterCodingException {
        final int j = offset + len;
        for (int i = offset; i < j; ++i)
            val[i] = codec.decode(buf);
    }

    static <T> void decodeArray(ByteBuffer buf, T[] val, Codec<T> codec)
            throws CharacterCodingException {
        decodeArray(buf, val, 0, val.length, codec);
    }

    public final void encode(ByteBuffer buf, ArraySlice<T> val)
            throws CharacterCodingException {
        if (val.getLength() != size)
            throw new IllegalArgumentException();
        encodeArray(buf, val.getBuffer(), val.getOffset(), val.getLength(),
                codec);
    }

    public final ArraySlice<T> decode(ByteBuffer buf)
            throws CharacterCodingException {
        final ArraySlice<T> val = new ArraySlice<T>(size);
        decodeArray(buf, val.getBuffer(), val.getOffset(), val.getLength(),
                codec);
        return val;
    }
}
