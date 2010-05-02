package org.openxdr;

import java.nio.ByteBuffer;

public class IntCodec implements Codec<Integer> {
    public final void encode(ByteBuffer buf, Integer val) {
        encodeInt(buf, val);
    }

    public final Integer decode(ByteBuffer buf) {
        return decodeInt(buf);
    }

    public static void encodeInt(ByteBuffer buf, int val) {
        buf.putInt(val);
    }

    public static int decodeInt(ByteBuffer buf) {
        return buf.getInt();
    }
}
