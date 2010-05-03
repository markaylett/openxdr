package org.openxdr;

import static org.openxdr.IntCodec.decodeInt;
import static org.openxdr.IntCodec.encodeInt;
import static org.openxdr.OpaqueCodec.decodeOpaque;
import static org.openxdr.OpaqueCodec.encodeOpaque;

import java.nio.ByteBuffer;

public class VarOpaqueCodec implements Codec<OpaqueSlice> {
    private final int maxsize;

    public VarOpaqueCodec(int maxsize) {
        this.maxsize = maxsize;

    }

    public VarOpaqueCodec() {
        this(Integer.MAX_VALUE);
    }

    public final void encode(ByteBuffer buf, OpaqueSlice val) {
        encodeVarOpaque(buf, val.getBuffer(), val.getOffset(), val.getLength(),
                maxsize);
    }

    public final OpaqueSlice decode(ByteBuffer buf) {
        return new OpaqueSlice(decodeVarOpaque(buf, maxsize));
    }

    public static void encodeVarOpaque(ByteBuffer buf, byte[] val, int offset,
            int len, int maxsize) {
        if (maxsize < len)
            throw new IllegalArgumentException();
        encodeInt(buf, len);
        encodeOpaque(buf, val, offset, len);
    }

    public static void encodeVarOpaque(ByteBuffer buf, byte[] val, int maxsize) {
        encodeVarOpaque(buf, val, 0, val.length, maxsize);
    }

    public static void encodeVarOpaque(ByteBuffer buf, byte[] val) {
        encodeVarOpaque(buf, val, Integer.MAX_VALUE);
    }

    public static byte[] decodeVarOpaque(ByteBuffer buf, int maxsize) {
        final int len = decodeInt(buf);
        if (maxsize < len)
            throw new IllegalArgumentException();
        final byte[] dst = new byte[len];
        decodeOpaque(buf, dst, 0, dst.length);
        return dst;
    }

    public static byte[] decodeVarOpaque(ByteBuffer buf) {
        return decodeVarOpaque(buf, Integer.MAX_VALUE);
    }
}