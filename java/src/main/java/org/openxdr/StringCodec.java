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

import static org.openxdr.IntCodec.decodeInt;
import static org.openxdr.IntCodec.encodeInt;
import static org.openxdr.Utility.decodeAlign;
import static org.openxdr.Utility.encodeAlign;
import static org.openxdr.Utility.getUtf8Decoder;
import static org.openxdr.Utility.getUtf8Encoder;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

final class StringCodec implements Codec<CharBuffer> {
    private final int maxsize;

    StringCodec(int maxsize) {
        this.maxsize = maxsize;
    }

    StringCodec() {
        maxsize = Integer.MAX_VALUE;
    }

    static void encodeString(ByteBuffer buf, CharBuffer val, int maxsize)
            throws CharacterCodingException {
        final int len = val.length();
        if (maxsize < len)
            throw new IllegalArgumentException();
        encodeInt(buf, len);
        final CharsetEncoder encoder = getUtf8Encoder();
        final CoderResult result = encoder.encode(val, buf, true);
        if (!result.isUnderflow())
            result.throwException();
        encodeAlign(buf);
    }

    static void encodeString(ByteBuffer buf, CharBuffer val)
            throws CharacterCodingException {
        encodeString(buf, val, Integer.MAX_VALUE);
    }

    static void encodeString(ByteBuffer buf, String val, int maxsize)
            throws CharacterCodingException {
        encodeString(buf, CharBuffer.wrap(val), maxsize);
    }

    static void encodeString(ByteBuffer buf, String val)
            throws CharacterCodingException {
        encodeString(buf, CharBuffer.wrap(val));
    }

    static CharBuffer decodeString(ByteBuffer buf, int maxsize)
            throws CharacterCodingException {
        final int len = decodeInt(buf);
        if (maxsize < len)
            throw new IllegalArgumentException();
        final CharBuffer val = CharBuffer.allocate(len);
        final CharsetDecoder decoder = getUtf8Decoder();
        final CoderResult result = decoder.decode(buf, val, true);
        if (0 != val.remaining())
            result.throwException();
        decodeAlign(buf);
        return (CharBuffer) val.flip();
    }

    static CharBuffer decodeString(ByteBuffer buf)
            throws CharacterCodingException {
        return decodeString(buf, Integer.MAX_VALUE);
    }

    public final void encode(ByteBuffer buf, CharBuffer val)
            throws CharacterCodingException {
        encodeString(buf, val, maxsize);
    }

    public final CharBuffer decode(ByteBuffer buf)
            throws CharacterCodingException {
        return decodeString(buf, maxsize);
    }
}
