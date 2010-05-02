package org.openxdr;

import static org.openxdr.IntCodec.decodeInt;
import static org.openxdr.IntCodec.encodeInt;
import static org.openxdr.OpaqueCodec.decodeOpaque;
import static org.openxdr.OpaqueCodec.encodeOpaque;

import java.nio.ByteBuffer;

public class BytesCodec implements Codec<Slice> {
    private final int maxsize;

    public BytesCodec(int maxsize) {
        this.maxsize = maxsize;

    }

    public BytesCodec() {
        maxsize = Integer.MAX_VALUE;
    }

    public final void encode(ByteBuffer buf, Slice val) {
        encodeBytes(buf, val.getBuffer(), val.getOffset(), val.getLength(),
                maxsize);
    }

    public final Slice decode(ByteBuffer buf) {
        return new Slice(decodeBytes(buf, maxsize));
    }

    static void encodeBytes(ByteBuffer buf, byte[] val, int offset, int len,
            int maxsize) {
        if (maxsize < len)
            throw new IllegalArgumentException();
        encodeInt(buf, len);
        encodeOpaque(buf, val, offset, len);
    }

    static void encodeBytes(ByteBuffer buf, byte[] val, int maxsize) {
        encodeBytes(buf, val, 0, val.length, maxsize);
    }

    static void encodeBytes(ByteBuffer buf, byte[] val) {
        encodeBytes(buf, val, Integer.MAX_VALUE);
    }

    static byte[] decodeBytes(ByteBuffer buf, int maxsize) {
        final int len = decodeInt(buf);
        if (maxsize < len)
            throw new IllegalArgumentException();
        final byte[] dst = new byte[len];
        decodeOpaque(buf, dst, 0, dst.length);
        return dst;
    }

    static byte[] decodeBytes(ByteBuffer buf) {
        return decodeBytes(buf, Integer.MAX_VALUE);
    }
}