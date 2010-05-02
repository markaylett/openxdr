package org.openxdr;

import java.nio.ByteBuffer;

public class FloatCodec implements Codec<Float> {
    public final void encode(ByteBuffer buf, Float val) {
        encodeFloat(buf, val);
    }

    public final Float decode(ByteBuffer buf) {
        return decodeFloat(buf);
    }

    public static void encodeFloat(ByteBuffer buf, float val) {
        buf.putFloat(val);
    }

    public static float decodeFloat(ByteBuffer buf) {
        return buf.getFloat();
    }
}
