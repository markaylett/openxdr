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
import static org.openxdr.StringCodec.decodeString;
import static org.openxdr.StringCodec.encodeString;
import static org.openxdr.VarOpaqueCodec.decodeVarOpaque;
import static org.openxdr.VarOpaqueCodec.encodeVarOpaque;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;

import junit.framework.TestCase;

public final class Test extends TestCase {
    private static ByteBuffer newXdrBuffer(int capacity) {
        final ByteBuffer buf = ByteBuffer.allocate(capacity);
        buf.order(ByteOrder.BIG_ENDIAN);
        return buf;
    }

    public static void main(String[] args) throws Exception {
        final ByteBuffer buf = newXdrBuffer(1024);
        final FileInputStream is = new FileInputStream(
                "u:/src/c/xdrtest/test1.out");
        try {
            final FileChannel fc = is.getChannel();
            final int n = fc.read(buf);
            System.out.println(n);
            buf.flip();
            while (decodeBool(buf))
                System.out.println(decodeString(buf));
        } finally {
            is.close();
        }
    }

    public final void testInt() {
        final ByteBuffer buf = newXdrBuffer(4);
        encodeInt(buf, Integer.MIN_VALUE);
        buf.flip();
        assertEquals(Integer.MIN_VALUE, decodeInt(buf));
    }

    public final void testBool() {
        final ByteBuffer buf = newXdrBuffer(4);
        encodeBool(buf, true);
        buf.flip();
        assertEquals(true, decodeBool(buf));
    }

    public final void testHyper() {
        final ByteBuffer buf = newXdrBuffer(8);
        encodeHyper(buf, Long.MIN_VALUE);
        buf.flip();
        assertEquals(Long.MIN_VALUE, decodeHyper(buf));
    }

    public final void testFloat() {
        final ByteBuffer buf = newXdrBuffer(4);
        encodeFloat(buf, Float.MIN_VALUE);
        buf.flip();
        assertEquals(Float.MIN_VALUE, decodeFloat(buf));
    }

    public final void testDouble() {
        final ByteBuffer buf = newXdrBuffer(8);
        encodeDouble(buf, Double.MIN_VALUE);
        buf.flip();
        assertEquals(Double.MIN_VALUE, decodeDouble(buf));
    }

    public final void testOpaque() throws UnsupportedEncodingException {
        final ByteBuffer buf = newXdrBuffer(4);
        encodeOpaque(buf, "test".getBytes("UTF-8"));
        buf.flip();
        final byte[] out = new byte[4];
        decodeOpaque(buf, out);
        assertEquals("test", new String(out, "UTF-8"));
    }

    public final void testVarOpaque() throws UnsupportedEncodingException {
        final ByteBuffer buf = newXdrBuffer(8);
        encodeVarOpaque(buf, "test".getBytes("UTF-8"));
        buf.flip();
        assertEquals("test", new String(decodeVarOpaque(buf), "UTF-8"));
    }

    public final void testString() throws CharacterCodingException {
        final ByteBuffer buf = newXdrBuffer(8);
        encodeString(buf, CharBuffer.wrap("test"));
        buf.flip();
        assertEquals("test", decodeString(buf).toString());
    }
}
