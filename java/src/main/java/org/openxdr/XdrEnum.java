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

public final class XdrEnum {

    private static <T extends Enum<T>> T valueOf(int val, T[] values) {
        for (final T x : values)
            if (val == x.ordinal())
                return x;
        throw new IllegalArgumentException();
    }

    private XdrEnum() {
    }

    public static <T extends Enum<T>> void encode(ByteBuffer buf, T val) {
        XdrInt.encode(buf, val.ordinal());
    }

    public static <T extends Enum<T>> T decode(ByteBuffer buf, T[] values) {
        return valueOf(XdrInt.decode(buf), values);
    }

    public static <T extends Enum<T>> Codec<T> newCodec(final T[] values) {
        return new Codec<T>() {
            public final void encode(ByteBuffer buf, T val) {
                XdrEnum.encode(buf, val);
            }

            public final T decode(ByteBuffer buf) {
                return XdrEnum.decode(buf, values);
            }
        };
    }
}
