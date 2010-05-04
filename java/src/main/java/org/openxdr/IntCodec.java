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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class IntCodec implements Codec<Integer> {
    private static final IntCodec instance = new IntCodec();

    private IntCodec() {
    }

    public final void encode(ByteBuffer buf, Integer val) {
        encodeInt(buf, val);
    }

    public final Integer decode(ByteBuffer buf) {
        return decodeInt(buf);
    }

    public static IntCodec getInstance() {
        return instance;
    }

    public static void encodeInt(ByteBuffer buf, int val) {
        assert ByteOrder.BIG_ENDIAN == buf.order();
        buf.putInt(val);
    }

    public static int decodeInt(ByteBuffer buf) {
        assert ByteOrder.BIG_ENDIAN == buf.order();
        return buf.getInt();
    }
}
