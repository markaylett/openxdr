package org.openxdr;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

public interface Codec<T> {
    void encode(ByteBuffer buf, T val) throws CharacterCodingException;

    T decode(ByteBuffer buf) throws CharacterCodingException;
}
