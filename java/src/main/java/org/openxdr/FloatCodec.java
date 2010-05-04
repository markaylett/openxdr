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

public final class FloatCodec implements Codec<Float> {
    private static final FloatCodec instance = new FloatCodec();

    private FloatCodec() {
    }

    public final void encode(ByteBuffer buf, Float val) {
        encodeFloat(buf, val);
    }

    public final Float decode(ByteBuffer buf) {
        return decodeFloat(buf);
    }

    public static FloatCodec getInstance() {
        return instance;
    }

    public static void encodeFloat(ByteBuffer buf, float val) {
        assert ByteOrder.BIG_ENDIAN == buf.order();
        buf.putFloat(val);
    }

    public static float decodeFloat(ByteBuffer buf) {
        assert ByteOrder.BIG_ENDIAN == buf.order();
        return buf.getFloat();
    }
}
