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
