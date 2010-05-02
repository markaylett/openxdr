package org.openxdr;

import static org.openxdr.BoolCodec.decodeBool;
import static org.openxdr.BoolCodec.encodeBool;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

public class PointerCodec<T> implements Codec<T> {
	private final Codec<T> codec;

	public PointerCodec(Codec<T> codec) {
		this.codec = codec;
	}

	public final void encode(ByteBuffer buf, T val)
			throws CharacterCodingException {
		encodePointer(buf, val, codec);
	}

	public final T decode(ByteBuffer buf) throws CharacterCodingException {
		return decodePointer(buf, codec);
	}

	public static <T> void encodePointer(ByteBuffer buf, T val, Codec<T> codec)
			throws CharacterCodingException {
		if (null != val) {
			encodeBool(buf, true);
			codec.encode(buf, val);
		} else
			encodeBool(buf, false);
	}

	public static <T> T decodePointer(ByteBuffer buf, Codec<T> codec)
			throws CharacterCodingException {
		T val = null;
		if (decodeBool(buf))
			val = codec.decode(buf);
		return val;
	}
}
