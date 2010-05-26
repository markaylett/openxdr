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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

import junit.framework.TestCase;

public final class Test extends TestCase {

    public final void testInt() {
        final int val = Integer.MIN_VALUE;
        assertEquals(4, XdrInt.CODEC.size(val));
        final ByteBuffer buf = XdrBuffer.allocate(4);
        XdrInt.encode(buf, val);
        buf.flip();
        assertEquals(val, XdrInt.decode(buf));
    }

    public final void testBool() {
        final boolean val = true;
        assertEquals(4, XdrBool.CODEC.size(val));
        final ByteBuffer buf = XdrBuffer.allocate(4);
        XdrBool.encode(buf, val);
        buf.flip();
        assertEquals(val, XdrBool.decode(buf));
    }

    public final void testHyper() {
        final long val = Long.MIN_VALUE;
        assertEquals(8, XdrHyper.CODEC.size(val));
        final ByteBuffer buf = XdrBuffer.allocate(8);
        XdrHyper.encode(buf, val);
        buf.flip();
        assertEquals(val, XdrHyper.decode(buf));
    }

    public final void testFloat() {
        final float val = Float.MIN_VALUE;
        assertEquals(4, XdrFloat.CODEC.size(val));
        final ByteBuffer buf = XdrBuffer.allocate(4);
        XdrFloat.encode(buf, val);
        buf.flip();
        assertEquals(val, XdrFloat.decode(buf));
    }

    public final void testDouble() {
        final double val = Double.MIN_VALUE;
        assertEquals(8, XdrDouble.CODEC.size(val));
        final ByteBuffer buf = XdrBuffer.allocate(8);
        XdrDouble.encode(buf, val);
        buf.flip();
        assertEquals(val, XdrDouble.decode(buf));
    }

    public final void testOpaque() throws UnsupportedEncodingException {
        final byte[] val = "test".getBytes("UTF-8");
        assertEquals(4, XdrOpaque.newCodec(4).size(new Opaque(val)));
        final ByteBuffer buf = XdrBuffer.allocate(4);
        XdrOpaque.encode(buf, val);
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
