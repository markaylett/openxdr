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

public final class XdrFloat {

    private XdrFloat() {
    }

    public static void encode(ByteBuffer buf, float val) {
        assert ByteOrder.BIG_ENDIAN == buf.order();
        buf.putFloat(val);
    }

    public static float decode(ByteBuffer buf) {
        assert ByteOrder.BIG_ENDIAN == buf.order();
        return buf.getFloat();
    }

    public static final int SIZE = 4;

    public static final Codec<Float> CODEC = new Codec<Float>() {
        public final void encode(ByteBuffer buf, Float val) {
            XdrFloat.encode(buf, val);
        }

        public final Float decode(ByteBuffer buf) {
            return XdrFloat.decode(buf);
        }

        public final int size(Float val) {
            return SIZE;
        }
    };
}
