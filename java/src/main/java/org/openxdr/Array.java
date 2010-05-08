/**
 * Copyright 2010 Mark Aylett <mark.aylett@gmail.com>
 * 
 * The contents of this file are subject to the Common Development and
 * Distribution License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.sun.com/cddl/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 */
package org.openxdr;

public final class Array<T> {
    private final T[] buf;
    private final int offset;
    private final int len;

    public Array(T[] buf, int offset, int len) {
        this.buf = buf;
        this.offset = offset;
        this.len = len;
    }

    public Array(T[] buf) {
        this(buf, 0, buf.length);
    }

    @SuppressWarnings("unchecked")
    public Array(int size) {
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
