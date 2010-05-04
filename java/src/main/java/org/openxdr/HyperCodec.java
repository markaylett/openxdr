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

public final class HyperCodec implements Codec<Long> {
    private static final HyperCodec instance = new HyperCodec();

    private HyperCodec() {
    }

    public final void encode(ByteBuffer buf, Long val) {
        encodeHyper(buf, val);
    }

    public final Long decode(ByteBuffer buf) {
        return decodeHyper(buf);
    }

    public static HyperCodec getInstance() {
        return instance;
    }

    public static void encodeHyper(ByteBuffer buf, long val) {
        assert ByteOrder.BIG_ENDIAN == buf.order();
        buf.putLong(val);
    }

    public static long decodeHyper(ByteBuffer buf) {
        assert ByteOrder.BIG_ENDIAN == buf.order();
        return buf.getLong();
    }
}
