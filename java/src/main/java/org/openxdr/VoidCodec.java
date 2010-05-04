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

final class VoidCodec implements Codec<Void> {

    VoidCodec() {
    }

    static void encodeVoid(ByteBuffer buf, Void val) {
    }

    static Void decodeVoid(ByteBuffer buf) {
        return Void.VALUE;
    }

    public final void encode(ByteBuffer buf, Void val) {
        encodeVoid(buf, val);
    }

    public final Void decode(ByteBuffer buf) {
        return decodeVoid(buf);
    }
}
