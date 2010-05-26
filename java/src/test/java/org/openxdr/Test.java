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

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;

import junit.framework.TestCase;

public final class Test extends TestCase {
    interface StringEntry {
        String getItem();

        StringEntry getNext();
    }

    public static final class XdrStringEntry {

        private XdrStringEntry() {
        }

        static final Codec<StringEntry> CODEC = new Codec<StringEntry>() {

            public final void encode(ByteBuffer buf, StringEntry val)
                    throws CharacterCodingException {
                ITEM_CODEC.encode(buf, val.getItem());
                NEXT_CODEC.encode(buf, val.getNext());
            }

            public final StringEntry decode(ByteBuffer buf)
                    throws CharacterCodingException {
                final String item = ITEM_CODEC.decode(buf);
                final StringEntry next = NEXT_CODEC.decode(buf);
                return new StringEntry() {
                    public final String getItem() {
                        return item;
                    }

                    public final StringEntry getNext() {
                        return next;
                    }
                };
            }

            public final int size(StringEntry val) {
                return 0;
            }
        };

        private static final Codec<String> ITEM_CODEC = XdrString.VAR_CODEC;
        private static final Codec<StringEntry> NEXT_CODEC = XdrOptional
                .newCodec(CODEC);
    }

    public static final class XdrStringList {

        private XdrStringList() {
        }

        public static final Codec<StringEntry> CODEC = new Codec<StringEntry>() {

            public final void encode(ByteBuffer buf, StringEntry val)
                    throws CharacterCodingException {
                XdrOptional.encode(buf, val, XdrStringEntry.CODEC);
            }

            public final StringEntry decode(ByteBuffer buf)
                    throws CharacterCodingException {
                return XdrOptional.decode(buf, XdrStringEntry.CODEC);
            }

            public final int size(StringEntry val) {
                return 0;
            }
        };
    }

    interface Error2 {
        Long getSubcode();

        String getMessage();
    }

    public static final class XdrError2 {

        private XdrError2() {
        }

        public static final Codec<Error2> CODEC = new Codec<Error2>() {
            public final void encode(ByteBuffer buf, Error2 val)
                    throws CharacterCodingException {
                XdrHyper.encode(buf, val.getSubcode());
                XdrString.encode(buf, val.getMessage());
            }

            public final Error2 decode(ByteBuffer buf)
                    throws CharacterCodingException {
                final Long subcode = XdrHyper.decode(buf);
                final String message = XdrString.decode(buf);
                return new Error2() {
                    public final Long getSubcode() {
                        return subcode;
                    }

                    public final String getMessage() {
                        return message;
                    }
                };
            }

            public final int size(Error2 val) {
                return 0;
            }
        };
    }

    public static void main(String[] args) throws Exception {
        final ByteBuffer buf = XdrBuffer.allocate(1024);
        final FileInputStream is = new FileInputStream(
                "u:/src/c/xdrtest/test1.out");
        try {
            final FileChannel fc = is.getChannel();
            final int n = fc.read(buf);
            System.out.println(n);
            buf.flip();
            final StringEntry entry = XdrStringList.CODEC.decode(buf);
            System.out.println(entry.getItem());
        } finally {
            is.close();
        }
    }

    public final void testInt() {
        final ByteBuffer buf = XdrBuffer.allocate(4);
        XdrInt.encode(buf, Integer.MIN_VALUE);
        buf.flip();
        assertEquals(Integer.MIN_VALUE, XdrInt.decode(buf));
    }

    public final void testBool() {
        final ByteBuffer buf = XdrBuffer.allocate(4);
        XdrBool.encode(buf, true);
        buf.flip();
        assertEquals(true, XdrBool.decode(buf));
    }

    public final void testHyper() {
        final ByteBuffer buf = XdrBuffer.allocate(8);
        XdrHyper.encode(buf, Long.MIN_VALUE);
        buf.flip();
        assertEquals(Long.MIN_VALUE, XdrHyper.decode(buf));
    }

    public final void testFloat() {
        final ByteBuffer buf = XdrBuffer.allocate(4);
        XdrFloat.encode(buf, Float.MIN_VALUE);
        buf.flip();
        assertEquals(Float.MIN_VALUE, XdrFloat.decode(buf));
    }

    public final void testDouble() {
        final ByteBuffer buf = XdrBuffer.allocate(8);
        XdrDouble.encode(buf, Double.MIN_VALUE);
        buf.flip();
        assertEquals(Double.MIN_VALUE, XdrDouble.decode(buf));
    }

    public final void testOpaque() throws UnsupportedEncodingException {
        final ByteBuffer buf = XdrBuffer.allocate(4);
        XdrOpaque.encode(buf, "test".getBytes("UTF-8"));
        buf.flip();
        final byte[] out = new byte[4];
        XdrOpaque.decode(buf, out);
        assertEquals("test", new String(out, "UTF-8"));
    }

    public final void testVarOpaque() throws UnsupportedEncodingException {
        final ByteBuffer buf = XdrBuffer.allocate(8);
        XdrOpaque.encodeVar(buf, "test".getBytes("UTF-8"));
        buf.flip();
        assertEquals("test", new String(XdrOpaque.decodeVar(buf), "UTF-8"));
    }

    public final void testString() throws CharacterCodingException {
        final ByteBuffer buf = XdrBuffer.allocate(8);
        XdrString.encode(buf, "test");
        buf.flip();
        assertEquals("test", XdrString.decode(buf));
    }
}
