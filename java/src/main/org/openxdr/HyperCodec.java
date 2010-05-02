package org.openxdr;

import java.nio.ByteBuffer;

public class HyperCodec implements Codec<Long> {
    public final void encode(ByteBuffer buf, Long val) {
        encodeHyper(buf, val);
    }

    public final Long decode(ByteBuffer buf) {
        return decodeHyper(buf);
    }

    public static void encodeHyper(ByteBuffer buf, long val) {
        buf.putLong(val);
    }

    public static long decodeHyper(ByteBuffer buf) {
        return buf.getLong();
    }
}
