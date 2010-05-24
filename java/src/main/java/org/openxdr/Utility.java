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

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

final class Utility {
    private static final int[] ALIGN = { 0, 3, 2, 1 };
    private static final byte[] PADDING = { 0, 0, 0 };

    private static final class CharsetCodec {
        final CharsetEncoder encoder;
        final CharsetDecoder decoder;

        CharsetCodec(String charsetName) {
            final Charset charset = Charset.forName(charsetName);
            encoder = charset.newEncoder();
            decoder = charset.newDecoder();
        }
    }

    private static final ThreadLocal<CharsetCodec> UTF8 = new ThreadLocal<CharsetCodec>() {
        @Override
        protected final CharsetCodec initialValue() {
            return new CharsetCodec("UTF-8");
        }
    };

    private static int alignPos(int pos) {
        return pos + ALIGN[pos % 4];
    }

    private Utility() {
    }

    static CharsetEncoder getUtf8Encoder() {
        return UTF8.get().encoder;
    }

    static CharsetDecoder getUtf8Decoder() {
        return UTF8.get().decoder;
    }

    static void encodeAlign(ByteBuffer buf) {
        final int pos = buf.position();
        final int len = ALIGN[pos % 4];
        if (0 < len) {
            if (buf.hasArray()) {
                final int newPos = pos + len;
                if (buf.limit() < newPos)
                    throw new BufferOverflowException();
                System.arraycopy(PADDING, 0, buf.array(), pos, len);
                buf.position(newPos);
            } else
                buf.put(PADDING, 0, len);
        }
    }

    static void encodeAlign(ByteBuffer buf, byte[] val, int offset, int len) {
        if (buf.hasArray()) {
            final int pos = buf.position();
            final int newPos = pos + len;
            if (buf.limit() < newPos)
                throw new BufferOverflowException();
            System.arraycopy(val, offset, buf.array(), pos, len);
            buf.position(newPos);
        } else
            buf.put(val, offset, len);
        encodeAlign(buf);
    }

    static void decodeAlign(ByteBuffer buf) {
        final int pos = buf.position();
        final int newPos = alignPos(pos);
        if (buf.limit() < newPos)
            throw new BufferUnderflowException();
        buf.position(newPos);
    }

    static void decodeAlign(ByteBuffer buf, byte[] val, int offset, int len) {
        final int pos = buf.position();
        if (buf.hasArray()) {
            final int newPos = alignPos(pos + len);
            if (buf.limit() < newPos)
                throw new BufferUnderflowException();
            System.arraycopy(buf.array(), pos, val, offset, len);
            buf.position(newPos); // Aligned.
        } else {
            buf.get(val, offset, len);
            decodeAlign(buf);
        }
    }
}
