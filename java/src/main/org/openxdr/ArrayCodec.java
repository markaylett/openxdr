package org.openxdr;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

public class ArrayCodec<T> implements Codec<ArraySlice<T>> {
    private final Codec<T> codec;
    private final int size;

    public ArrayCodec(Codec<T> codec, int size) {
        this.codec = codec;
        this.size = size;
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

    public static <T> void encodeArray(ByteBuffer buf, T[] val, int offset,
            int len, Codec<T> codec) throws CharacterCodingException {
        final int j = offset + len;
        for (int i = offset; i < j; ++i)
            codec.encode(buf, val[i]);
    }

    public static <T> void encodeArray(ByteBuffer buf, T[] val, Codec<T> codec)
            throws CharacterCodingException {
        encodeArray(buf, val, 0, val.length, codec);
    }

    public static <T> void decodeArray(ByteBuffer buf, T[] val, int offset,
            int len, Codec<T> codec) throws CharacterCodingException {
        final int j = offset + len;
        for (int i = offset; i < j; ++i)
            val[i] = codec.decode(buf);
    }

    public static <T> void decodeArray(ByteBuffer buf, T[] val, Codec<T> codec)
            throws CharacterCodingException {
        decodeArray(buf, val, 0, val.length, codec);
    }
}