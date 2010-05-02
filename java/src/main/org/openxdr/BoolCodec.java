package org.openxdr;

import static org.openxdr.IntCodec.decodeInt;
import static org.openxdr.IntCodec.encodeInt;

import java.nio.ByteBuffer;

public class BoolCodec implements Codec<Boolean> {
    public final void encode(ByteBuffer buf, Boolean val) {
        encodeBool(buf, val);
    }

    public final Boolean decode(ByteBuffer buf) {
        return decodeBool(buf);
    }

    public static void encodeBool(ByteBuffer buf, boolean val) {
        encodeInt(buf, val ? 1 : 0);
    }

    public static boolean decodeBool(ByteBuffer buf) {
        return 0 != decodeInt(buf);
    }
}
