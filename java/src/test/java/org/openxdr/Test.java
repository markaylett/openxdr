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

final class StringEntry {
    public String item;
    public StringEntry next;
}

final class StringEntryCodec implements Codec<StringEntry> {

    public final void encode(ByteBuffer buf, StringEntry val)
            throws CharacterCodingException {
        XdrString.encode(buf, CharBuffer.wrap(val.item));
        XdrOptional.encode(buf, val.next, this);
    }

    public final StringEntry decode(ByteBuffer buf)
            throws CharacterCodingException {
        final StringEntry val = new StringEntry();
        val.item = XdrString.decode(buf).toString();
        val.next = XdrOptional.decode(buf, this);
        return val;
    }
}

final class StringListCodec implements Codec<StringEntry> {
    private static final StringEntryCodec CODEC = new StringEntryCodec();

    public final void encode(ByteBuffer buf, StringEntry val)
            throws CharacterCodingException {
        encodeStringList(buf, val);
    }

    public final StringEntry decode(ByteBuffer buf)
            throws CharacterCodingException {
        return decodeStringList(buf);
    }

    public static void encodeStringList(ByteBuffer buf, StringEntry val)
            throws CharacterCodingException {
        XdrOptional.encode(buf, val.next, CODEC);
    }

    public static StringEntry decodeStringList(ByteBuffer buf)
            throws CharacterCodingException {
        return XdrOptional.decode(buf, CODEC);
    }
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
            final StringEntry entry = StringListCodec.decodeStringList(buf);
            System.out.println(entry.item);
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
