package org.openxdr;

public final class OpaqueSlice {
    private final byte[] buf;
    private final int offset;
    private final int len;

    public OpaqueSlice(byte[] buf, int offset, int len) {
        this.buf = buf;
        this.offset = offset;
        this.len = len;
    }

    public OpaqueSlice(byte[] buf) {
        this(buf, 0, buf.length);
    }

    public OpaqueSlice(int size) {
        this(new byte[size]);
    }

    public final byte[] getBuffer() {
        return buf;
    }

    public final int getOffset() {
        return offset;
    }

    public final int getLength() {
        return len;
    }
}
