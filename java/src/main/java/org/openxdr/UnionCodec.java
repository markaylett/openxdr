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
import java.nio.charset.CharacterCodingException;
import java.util.Map;

public final class UnionCodec implements Codec<Union> {
    private final Map<Integer, Codec<?>> cases;
    private final Codec<?> def;

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

    public UnionCodec(Map<Integer, Codec<?>> cases, Codec<?> def) {
        this.cases = cases;
        this.def = def;
    }

    public UnionCodec(final Map<Integer, Codec<?>> cases) {
        this(cases, null);
    }

    public final void encode(ByteBuffer buf, Union val)
            throws CharacterCodingException {
        encodeUnion(buf, val, cases, def);
    }

    public final Union decode(ByteBuffer buf) throws CharacterCodingException {
        return decodeUnion(buf, cases, def);
    }

    @SuppressWarnings("unchecked")
    public static void encodeUnion(ByteBuffer buf, Union val,
            Map<Integer, Codec<?>> cases, Codec<?> def)
            throws CharacterCodingException {
        final Codec codec = getCodec(val.getType(), cases, def);
        encodeInt(buf, val.getType());
        codec.encode(buf, val.getValue());
    }

    public static void encodeUnion(ByteBuffer buf, Union val,
            Map<Integer, Codec<?>> cases) throws CharacterCodingException {
        encodeUnion(buf, val, cases, null);
    }

    @SuppressWarnings("unchecked")
    public static Union decodeUnion(ByteBuffer buf,
            Map<Integer, Codec<?>> cases, Codec<?> def)
            throws CharacterCodingException {
        final int type = decodeInt(buf);
        final Codec codec = getCodec(type, cases, def);
        return new Union(type, codec.decode(buf));
    }

    public static Union decodeUnion(ByteBuffer buf, Map<Integer, Codec<?>> cases)
            throws CharacterCodingException {
        return decodeUnion(buf, cases, null);
    }
}
