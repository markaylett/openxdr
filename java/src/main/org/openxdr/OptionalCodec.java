package org.openxdr;

import static org.openxdr.BoolCodec.decodeBool;
import static org.openxdr.BoolCodec.encodeBool;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

public class OptionalCodec<T> implements Codec<T> {
    private final Codec<T> codec;

    public OptionalCodec(Codec<T> codec) {
        this.codec = codec;
    }

    public final void encode(ByteBuffer buf, T val)
            throws CharacterCodingException {
        encodeOptional(buf, val, codec);
    }

    public final T decode(ByteBuffer buf) throws CharacterCodingException {
        return decodeOptional(buf, codec);
    }

    public static <T> void encodeOptional(ByteBuffer buf, T val, Codec<T> codec)
            throws CharacterCodingException {
        if (null != val) {
            encodeBool(buf, true);
            codec.encode(buf, val);
        } else
            encodeBool(buf, false);
    }

    public static <T> T decodeOptional(ByteBuffer buf, Codec<T> codec)
            throws CharacterCodingException {
        T val = null;
        if (decodeBool(buf))
            val = codec.decode(buf);
        return val;
    }
}
