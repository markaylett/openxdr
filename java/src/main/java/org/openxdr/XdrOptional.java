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

public final class XdrOptional {

    private XdrOptional() {
    }

    public static <T> void encode(ByteBuffer buf, T val, Codec<T> codec)
            throws CharacterCodingException {
        if (null != val) {
            XdrBool.encode(buf, true);
            codec.encode(buf, val);
        } else
            XdrBool.encode(buf, false);
    }

    public static <T> T decode(ByteBuffer buf, Codec<T> codec)
            throws CharacterCodingException {
        T val = null;
        if (XdrBool.decode(buf))
            val = codec.decode(buf);
        return val;
    }

    public static <T> int size(T val, Codec<T> codec) {
        final int n = null == val ? 0 : codec.size(val);
        return XdrBool.SIZE + n;
    }

    public static <T> Codec<T> newCodec(final Codec<T> codec) {
        return new Codec<T>() {
            public final void encode(ByteBuffer buf, T val)
                    throws CharacterCodingException {
                XdrOptional.encode(buf, val, codec);
            }

            public final T decode(ByteBuffer buf)
                    throws CharacterCodingException {
                return XdrOptional.decode(buf, codec);
            }

            public final int size(T val) {
                return XdrOptional.size(val, codec);
            }
        };
    }
}
