package org.openxdr;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class XdrBuffer {
    private XdrBuffer() {
    }

    public static ByteBuffer allocateDirect(int capacity) {
        final ByteBuffer buf = ByteBuffer.allocateDirect(capacity);
        buf.order(ByteOrder.BIG_ENDIAN);
        return buf;
    }

    public static ByteBuffer allocate(int capacity) {
        final ByteBuffer buf = ByteBuffer.allocate(capacity);
        buf.order(ByteOrder.BIG_ENDIAN);
        return buf;
    }

    public static ByteBuffer wrap(byte[] array, int offset, int length) {
        final ByteBuffer buf = ByteBuffer.wrap(array, offset, length);
        buf.order(ByteOrder.BIG_ENDIAN);
        return buf;
    }

    public static ByteBuffer wrap(byte[] array) {
        final ByteBuffer buf = ByteBuffer.wrap(array);
        buf.order(ByteOrder.BIG_ENDIAN);
        return buf;
    }
}
