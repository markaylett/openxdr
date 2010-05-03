package org.openxdr;

import static org.openxdr.Utility.decodeAlign;
import static org.openxdr.Utility.encodeAlign;

import java.nio.ByteBuffer;

public class OpaqueCodec implements Codec<OpaqueSlice> {
    private final int size;

    public OpaqueCodec(int size) {
        this.size = size;

    }

    public final void encode(ByteBuffer buf, OpaqueSlice val) {
        if (val.getLength() != size)
            throw new IllegalArgumentException();
        encodeOpaque(buf, val.getBuffer(), val.getOffset(), val.getLength());
    }

    public final OpaqueSlice decode(ByteBuffer buf) {
        final OpaqueSlice val = new OpaqueSlice(size);
        decodeOpaque(buf, val.getBuffer(), val.getOffset(), val.getLength());
        return val;
    }

    public static void encodeOpaque(ByteBuffer buf, byte[] val, int offset,
            int len) {
        encodeAlign(buf, val, offset, len);
    }

    public static void encodeOpaque(ByteBuffer buf, byte[] val) {
        encodeOpaque(buf, val, 0, val.length);
    }

    public static void decodeOpaque(ByteBuffer buf, byte[] val, int offset,
            int len) {
        decodeAlign(buf, val, offset, len);
    }

    public static void decodeOpaque(ByteBuffer buf, byte[] val) {
        decodeOpaque(buf, val, 0, val.length);
    }
}