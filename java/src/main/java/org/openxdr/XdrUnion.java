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
import java.nio.charset.CharacterCodingException;
import java.util.HashMap;
import java.util.Map;

public final class XdrUnion {

    private static Codec<?> getCodec(int type, Map<Integer, Codec<?>> cases,
            Codec<?> def) {
        Codec<?> codec = cases.get(type);
        if (null == codec) {
            if (null == def)
                throw new IllegalArgumentException();
            codec = def;
        }
        return codec;
    }

    private XdrUnion() {
    }

    @SuppressWarnings("unchecked")
    public static void encode(ByteBuffer buf, Union val,
            Map<Integer, Codec<?>> cases, Codec<?> def)
            throws CharacterCodingException {
        final Codec codec = getCodec(val.getType(), cases, def);
        XdrInt.encode(buf, val.getType());
        codec.encode(buf, val.getValue());
    }

    public static void encode(ByteBuffer buf, Union val,
            Map<Integer, Codec<?>> cases) throws CharacterCodingException {
        encode(buf, val, cases, null);
    }

    @SuppressWarnings("unchecked")
    public static Union decode(ByteBuffer buf, Map<Integer, Codec<?>> cases,
            Codec<?> def) throws CharacterCodingException {
        final int type = XdrInt.decode(buf);
        final Codec codec = getCodec(type, cases, def);
        return new Union(type, codec.decode(buf));
    }

    public static Union decode(ByteBuffer buf, Map<Integer, Codec<?>> cases)
            throws CharacterCodingException {
        return decode(buf, cases, null);
    }

    public static Codec<Union> newCodec(final Map<Integer, Codec<?>> cases,
            final Codec<?> def) {
        return new Codec<Union>() {
            public final void encode(ByteBuffer buf, Union val)
                    throws CharacterCodingException {
                XdrUnion.encode(buf, val, cases, def);
            }

            public final Union decode(ByteBuffer buf)
                    throws CharacterCodingException {
                return XdrUnion.decode(buf, cases, def);
            }
        };
    }

    public static Codec<Union> newCodec(Map<Integer, Codec<?>> cases) {
        return newCodec(cases, null);
    }

    public static Map<Integer, Codec<?>> newCases(Object... args) {
        final Map<Integer, Codec<?>> cases = new HashMap<Integer, Codec<?>>();
        for (int i = 0; i < args.length; i += 2)
            cases.put((Integer) args[i], (Codec<?>) args[i + 1]);
        return cases;
    }
}
