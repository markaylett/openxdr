package org.openxdr;

public final class ArraySlice<T> {
    private final T[] buf;
    private final int offset;
    private final int len;

    public ArraySlice(T[] buf, int offset, int len) {
        this.buf = buf;
        this.offset = offset;
        this.len = len;
    }

    public ArraySlice(T[] buf) {
        this(buf, 0, buf.length);
    }

    @SuppressWarnings("unchecked")
    public ArraySlice(int size) {
        this((T[]) new Object[size]);
    }

    public final T[] getBuffer() {
        return buf;
    }

    public final int getOffset() {
        return offset;
    }

    public final int getLength() {
        return len;
    }
}
