package org.openxdr;

import java.nio.ByteBuffer;

public class DoubleCodec implements Codec<Double> {
    public final void encode(ByteBuffer buf, Double val) {
        encodeDouble(buf, val);
    }

    public final Double decode(ByteBuffer buf) {
        return decodeDouble(buf);
    }

    public static void encodeDouble(ByteBuffer buf, double val) {
        buf.putDouble(val);
    }

    public static double decodeDouble(ByteBuffer buf) {
        return buf.getDouble();
    }
}
