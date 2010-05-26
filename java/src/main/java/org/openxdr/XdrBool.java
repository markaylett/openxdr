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

public final class XdrBool {

    private XdrBool() {
    }

    public static void encode(ByteBuffer buf, boolean val) {
        XdrInt.encode(buf, val ? 1 : 0);
    }

    public static boolean decode(ByteBuffer buf) {
        return 0 != XdrInt.decode(buf);
    }

    public static final int SIZE = XdrInt.SIZE;

    public static final Codec<Boolean> CODEC = new Codec<Boolean>() {
        public final void encode(ByteBuffer buf, Boolean val) {
            XdrBool.encode(buf, val);
        }

        public final Boolean decode(ByteBuffer buf) {
            return XdrBool.decode(buf);
        }

        public final int size(Boolean val) {
            return SIZE;
        }
    };
}
