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

import static org.openxdr.IntCodec.decodeInt;
import static org.openxdr.IntCodec.encodeInt;

import java.nio.ByteBuffer;

public final class BoolCodec implements Codec<Boolean> {
    private static final BoolCodec instance = new BoolCodec();

    private BoolCodec() {
    }

    public final void encode(ByteBuffer buf, Boolean val) {
        encodeBool(buf, val);
    }

    public final Boolean decode(ByteBuffer buf) {
        return decodeBool(buf);
    }

    public static BoolCodec getInstance() {
        return instance;
    }

    public static void encodeBool(ByteBuffer buf, boolean val) {
        encodeInt(buf, val ? 1 : 0);
    }

    public static boolean decodeBool(ByteBuffer buf) {
        return 0 != decodeInt(buf);
    }
}
