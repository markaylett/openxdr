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
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;

import junit.framework.TestCase;

final class Xdr {

    interface StringEntry {
        String getItem();

        StringEntry getNext();
    }

    static final Codec<StringEntry> STRING_ENTRY = new Codec<StringEntry>() {

        public final void encode(ByteBuffer buf, StringEntry val)
                throws CharacterCodingException {
            XdrString.encode(buf, val.getItem());
            XdrOptional.encode(buf, val.getNext(), this);
        }

        public final StringEntry decode(ByteBuffer buf)
                throws CharacterCodingException {
            final String item = XdrString.decode(buf).toString();
            final StringEntry next = XdrOptional.decode(buf, this);
            return new StringEntry() {
                public final String getItem() {
                    return item;
                }

                public final StringEntry getNext() {
                    return next;
                }
            };
        }
    };

    static final Codec<StringEntry> STRING_LIST = new Codec<StringEntry>() {

        public final void encode(ByteBuffer buf, StringEntry val)
                throws CharacterCodingException {
            XdrOptional.encode(buf, val, STRING_ENTRY);
        }

        public final StringEntry decode(ByteBuffer buf)
                throws CharacterCodingException {
            return XdrOptional.decode(buf, STRING_ENTRY);
        }
    };

    interface Error2 {
        Long getSubcode();

        String getMessage();
    }

    static final Codec<Error2> ERROR2 = new Codec<Error2>() {

        public final void encode(ByteBuffer buf, Error2 val)
                throws CharacterCodingException {
            XdrHyper.encode(buf, val.getSubcode());
            XdrString.encode(buf, val.getMessage());
        }

        public final Error2 decode(ByteBuffer buf)
                throws CharacterCodingException {
            final Long subcode = XdrHyper.decode(buf);
            final String message = XdrString.decode(buf).toString();
            return new Error2() {
                public final Long getSubcode() {
                    return subcode;
                }

                public final String getMessage() {
                    return message;
                }
            };
        }
    };
}

public final class Test extends TestCase {

    public static void main(String[] args) throws Exception {
        final ByteBuffer buf = XdrBuffer.allocate(1024);
        final FileInputStream is = new FileInputStream(
                "u:/src/c/xdrtest/test1.out");
        try {
            final FileChannel fc = is.getChannel();
            final int n = fc.read(buf);
            System.out.println(n);
            buf.flip();
            final Xdr.StringEntry entry = Xdr.STRING_LIST.decode(buf);
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
        XdrString.encode(buf, CharBuffer.wrap("test"));
        buf.flip();
        assertEquals("test", XdrString.decode(buf).toString());
    }
}
