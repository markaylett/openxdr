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

    @SuppressWarnings("unchecked")
    private static <T> Codec<Object> getCodec(T type,
            Map<T, Codec<?>> cases, Codec<?> def) {
        Codec<?> codec = cases.get(type);
        if (null == codec) {
            if (null == def)
                throw new IllegalArgumentException();
            codec = def;
        }
        return (Codec<Object>) codec;
    }

    private XdrUnion() {
    }

    public static <T> void encode(ByteBuffer buf, Union<T> val, Codec<T> sel,
            Map<T, Codec<?>> cases, Codec<?> def)
            throws CharacterCodingException {
        final Codec<Object> codec = getCodec(val.getType(), cases, def);
        sel.encode(buf, val.getType());
        codec.encode(buf, val.getValue());
    }

    public static <T> void encode(ByteBuffer buf, Union<T> val, Codec<T> sel,
            Map<T, Codec<?>> cases) throws CharacterCodingException {
        encode(buf, val, sel, cases, null);
    }

    public static <T> Union<T> decode(ByteBuffer buf, Codec<T> sel,
            Map<T, Codec<?>> cases, Codec<?> def)
            throws CharacterCodingException {
        final T type = sel.decode(buf);
        final Codec<?> codec = getCodec(type, cases, def);
        return new Union<T>(type, codec.decode(buf));
    }

    public static <T> Union<T> decode(ByteBuffer buf, Codec<T> sel,
            Map<T, Codec<?>> cases) throws CharacterCodingException {
        return decode(buf, sel, cases, null);
    }

    public static <T> Codec<Union<T>> newCodec(final Codec<T> sel,
            final Map<T, Codec<?>> cases, final Codec<?> def) {
        return new Codec<Union<T>>() {
            public final void encode(ByteBuffer buf, Union<T> val)
                    throws CharacterCodingException {
                XdrUnion.encode(buf, val, sel, cases, def);
            }

            public final Union<T> decode(ByteBuffer buf)
                    throws CharacterCodingException {
                return XdrUnion.decode(buf, sel, cases, def);
            }
        };
    }

    public static <T> Codec<Union<T>> newCodec(Codec<T> sel,
            Map<T, Codec<?>> cases) {
        return newCodec(sel, cases, null);
    }

    @SuppressWarnings("unchecked")
    public static<T> Map<T, Codec<?>> newCases(Object... args) {
        final Map<T, Codec<?>> cases = new HashMap<T, Codec<?>>();
        for (int i = 0; i < args.length; i += 2)
            cases.put((T) args[i], (Codec<?>) args[i + 1]);
        return cases;
    }
}
