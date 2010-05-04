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

import java.nio.CharBuffer;
import java.util.Map;

public final class Codecs {
    private Codecs() {
    }

    public static final Codec<Integer> INT = new IntCodec();
    public static final Codec<Boolean> BOOL = new BoolCodec();
    public static final Codec<Long> HYPER = new HyperCodec();
    public static final Codec<Float> FLOAT = new FloatCodec();
    public static final Codec<Double> DOUBLE = new DoubleCodec();

    public static Codec<OpaqueSlice> newOpaque(int size) {
        return new OpaqueCodec(size);
    }

    public static Codec<OpaqueSlice> newVarOpaque(int maxsize) {
        return new VarOpaqueCodec(maxsize);
    }

    public static final Codec<OpaqueSlice> VAR_OPAQUE = new VarOpaqueCodec();

    public static Codec<CharBuffer> newString(int maxsize) {
        return new StringCodec(maxsize);
    }

    public static final Codec<CharBuffer> STRING = new StringCodec();

    public static <T> Codec<ArraySlice<T>> newArray(Codec<T> codec, int size) {
        return new ArrayCodec<T>(codec, size);
    }

    public static <T> Codec<ArraySlice<T>> newVarArray(Codec<T> codec,
            int maxsize) {
        return new VarArrayCodec<T>(codec, maxsize);
    }

    public static <T> Codec<ArraySlice<T>> newVarArray(Codec<T> codec) {
        return new VarArrayCodec<T>(codec);
    }

    public static Codec<Union> newUnion(Map<Integer, Codec<?>> cases,
            Codec<?> def) {
        return new UnionCodec(cases, def);
    }

    public static Codec<Union> newUnion(Map<Integer, Codec<?>> cases) {
        return new UnionCodec(cases);
    }

    public static final Codec<Void> VOID = new VoidCodec();

    // 3.19 Optional-data

    public static <T> Codec<T> newOptional(Codec<T> codec) {
        return new OptionalCodec<T>(codec);
    }
}
