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
import static org.openxdr.Utility.getUtf8Decoder;
import static org.openxdr.Utility.getUtf8Encoder;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public final class XdrString {

    private XdrString() {
    }

    public static void encode(ByteBuffer buf, CharBuffer val, int maxsize)
            throws CharacterCodingException {
        final int len = val.length();
        if (maxsize < len)
            throw new IllegalArgumentException();
        XdrInt.encode(buf, len);
        final CharsetEncoder encoder = getUtf8Encoder();
        final CoderResult result = encoder.encode(val, buf, true);
        if (!result.isUnderflow())
            result.throwException();
        encodeAlign(buf);
    }

    public static void encode(ByteBuffer buf, CharBuffer val)
            throws CharacterCodingException {
        encode(buf, val, Integer.MAX_VALUE);
    }

    public static void encode(ByteBuffer buf, String val, int maxsize)
            throws CharacterCodingException {
        encode(buf, CharBuffer.wrap(val), maxsize);
    }

    public static void encode(ByteBuffer buf, String val)
            throws CharacterCodingException {
        encode(buf, CharBuffer.wrap(val));
    }

    public static CharBuffer decode(ByteBuffer buf, int maxsize)
            throws CharacterCodingException {
        final int len = XdrInt.decode(buf);
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

    public static CharBuffer decode(ByteBuffer buf)
            throws CharacterCodingException {
        return decode(buf, Integer.MAX_VALUE);
    }

    public static Codec<CharBuffer> newCodec(final int maxsize) {
        return new Codec<CharBuffer>() {
            public final void encode(ByteBuffer buf, CharBuffer val)
                    throws CharacterCodingException {
                XdrString.encode(buf, val, maxsize);
            }

            public final CharBuffer decode(ByteBuffer buf)
                    throws CharacterCodingException {
                return XdrString.decode(buf, maxsize);
            }
        };
    }

    public static final Codec<CharBuffer> CODEC = newCodec(Integer.MAX_VALUE);
}
