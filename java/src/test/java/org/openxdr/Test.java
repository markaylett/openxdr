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

import static org.openxdr.BoolCodec.decodeBool;
import static org.openxdr.BoolCodec.encodeBool;
import static org.openxdr.DoubleCodec.decodeDouble;
import static org.openxdr.DoubleCodec.encodeDouble;
import static org.openxdr.FloatCodec.decodeFloat;
import static org.openxdr.FloatCodec.encodeFloat;
import static org.openxdr.HyperCodec.decodeHyper;
import static org.openxdr.HyperCodec.encodeHyper;
import static org.openxdr.IntCodec.decodeInt;
import static org.openxdr.IntCodec.encodeInt;
import static org.openxdr.OpaqueCodec.decodeOpaque;
import static org.openxdr.OpaqueCodec.encodeOpaque;
import static org.openxdr.OptionalCodec.decodeOptional;
import static org.openxdr.OptionalCodec.encodeOptional;
import static org.openxdr.StringCodec.decodeString;
import static org.openxdr.StringCodec.encodeString;
import static org.openxdr.VarOpaqueCodec.decodeVarOpaque;
import static org.openxdr.VarOpaqueCodec.encodeVarOpaque;

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
        encodeString(buf, CharBuffer.wrap(val.item));
        encodeOptional(buf, val.next, this);
    }

    public final StringEntry decode(ByteBuffer buf)
            throws CharacterCodingException {
        final StringEntry val = new StringEntry();
        val.item = decodeString(buf).toString();
        val.next = decodeOptional(buf, this);
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
        encodeOptional(buf, val.next, CODEC);
    }

    public static StringEntry decodeStringList(ByteBuffer buf)
            throws CharacterCodingException {
        return decodeOptional(buf, CODEC);
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
        encodeInt(buf, Integer.MIN_VALUE);
        buf.flip();
        assertEquals(Integer.MIN_VALUE, decodeInt(buf));
    }

    public final void testBool() {
        final ByteBuffer buf = XdrBuffer.allocate(4);
        encodeBool(buf, true);
        buf.flip();
        assertEquals(true, decodeBool(buf));
    }

    public final void testHyper() {
        final ByteBuffer buf = XdrBuffer.allocate(8);
        encodeHyper(buf, Long.MIN_VALUE);
        buf.flip();
        assertEquals(Long.MIN_VALUE, decodeHyper(buf));
    }

    public final void testFloat() {
        final ByteBuffer buf = XdrBuffer.allocate(4);
        encodeFloat(buf, Float.MIN_VALUE);
        buf.flip();
        assertEquals(Float.MIN_VALUE, decodeFloat(buf));
    }

    public final void testDouble() {
        final ByteBuffer buf = XdrBuffer.allocate(8);
        encodeDouble(buf, Double.MIN_VALUE);
        buf.flip();
        assertEquals(Double.MIN_VALUE, decodeDouble(buf));
    }

    public final void testOpaque() throws UnsupportedEncodingException {
        final ByteBuffer buf = XdrBuffer.allocate(4);
        encodeOpaque(buf, "test".getBytes("UTF-8"));
        buf.flip();
        final byte[] out = new byte[4];
        decodeOpaque(buf, out);
        assertEquals("test", new String(out, "UTF-8"));
    }

    public final void testVarOpaque() throws UnsupportedEncodingException {
        final ByteBuffer buf = XdrBuffer.allocate(8);
        encodeVarOpaque(buf, "test".getBytes("UTF-8"));
        buf.flip();
        assertEquals("test", new String(decodeVarOpaque(buf), "UTF-8"));
    }

    public final void testString() throws CharacterCodingException {
        final ByteBuffer buf = XdrBuffer.allocate(8);
        encodeString(buf, CharBuffer.wrap("test"));
        buf.flip();
        assertEquals("test", decodeString(buf).toString());
    }
}
