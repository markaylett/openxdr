package org.openxdr;

import static org.openxdr.Utility.decodeAlign;
import static org.openxdr.Utility.encodeAlign;

import java.nio.ByteBuffer;

public class OpaqueCodec implements Codec<Slice> {
    private final int size;

    public OpaqueCodec(int size) {
        this.size = size;

    }

    public final void encode(ByteBuffer buf, Slice val) {
        encodeOpaque(buf, val.getBuffer(), val.getOffset(), val.getLength());
    }

    public final Slice decode(ByteBuffer buf) {
        final Slice val = new Slice(size);
        decodeOpaque(buf, val.getBuffer(), val.getOffset(), val.getLength());
        return val;
    }

    static void encodeOpaque(ByteBuffer buf, byte[] val, int offset, int len) {
        encodeAlign(buf, val, offset, len);
    }

    static void encodeOpaque(ByteBuffer buf, byte[] val) {
        encodeOpaque(buf, val, 0, val.length);
    }

    static byte[] decodeOpaque(ByteBuffer buf, byte[] val, int offset, int len) {
        return decodeAlign(buf, val, offset, len);
    }

    static byte[] decodeOpaque(ByteBuffer buf, byte[] val) {
        return decodeOpaque(buf, val, 0, val.length);
    }
}