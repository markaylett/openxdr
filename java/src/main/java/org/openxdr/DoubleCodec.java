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

final class DoubleCodec implements Codec<Double> {

    DoubleCodec() {
    }

    static void encodeDouble(ByteBuffer buf, double val) {
        assert ByteOrder.BIG_ENDIAN == buf.order();
        buf.putDouble(val);
    }

    static double decodeDouble(ByteBuffer buf) {
        assert ByteOrder.BIG_ENDIAN == buf.order();
        return buf.getDouble();
    }

    public final void encode(ByteBuffer buf, Double val) {
        encodeDouble(buf, val);
    }

    public final Double decode(ByteBuffer buf) {
        return decodeDouble(buf);
    }
}
