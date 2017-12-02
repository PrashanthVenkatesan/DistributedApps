package com.github.zookeeper.DUID;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public final class Convertor {
	public static final Set<Class<?>> PRIMITIVES = new HashSet<Class<?>>();
	static {
		Convertor.PRIMITIVES.add(String.class);
		Convertor.PRIMITIVES.add(Byte.class);
		Convertor.PRIMITIVES.add(byte.class);
		Convertor.PRIMITIVES.add(Character.class);
		Convertor.PRIMITIVES.add(char.class);
		Convertor.PRIMITIVES.add(Short.class);
		Convertor.PRIMITIVES.add(short.class);
		Convertor.PRIMITIVES.add(Integer.class);
		Convertor.PRIMITIVES.add(int.class);
		Convertor.PRIMITIVES.add(Float.class);
		Convertor.PRIMITIVES.add(float.class);
		Convertor.PRIMITIVES.add(Double.class);
		Convertor.PRIMITIVES.add(double.class);
		Convertor.PRIMITIVES.add(Long.class);
		Convertor.PRIMITIVES.add(long.class);
		Convertor.PRIMITIVES.add(Boolean.class);
		Convertor.PRIMITIVES.add(boolean.class);
		Convertor.PRIMITIVES.add(BigDecimal.class);
		Convertor.PRIMITIVES.add(BigInteger.class);
	}

	public static Object bytesToObject(final byte[] bytes, final Class<?> objectType)
			throws IOException, ClassNotFoundException {
		Object object = null;
		if (bytes != null && bytes.length != 0) {
			if (Convertor.PRIMITIVES.contains(objectType)) {
				object = Convertor.convert(objectType, new String(bytes));
			} else {
				InputStream inputStream = null;
				ObjectInputStream objectInputStream = null;
				try {
					inputStream = new ByteArrayInputStream(bytes);
					objectInputStream = new ObjectInputStream(inputStream);
					object = objectInputStream.readObject();
				} finally {
					Convertor.close(inputStream);
					Convertor.close(objectInputStream);
				}
			}
		} else
			throw new IllegalArgumentException("Cannot object-transform null or empty byte array");

		return object;
	}

	public static byte[] objectToBytes(final Object object, final Class<?> objectType) throws IOException {
		byte[] bytes = null;
		if (object != null) {
			if (Convertor.PRIMITIVES.contains(objectType))
				bytes = object.toString().getBytes();
			else {
				final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ObjectOutputStream objectOutputStream = null;
				try {
					objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
					objectOutputStream.writeObject(object);
					bytes = byteArrayOutputStream.toByteArray();
				} finally {
					Convertor.close(byteArrayOutputStream);
					Convertor.close(objectOutputStream);
				}
			}
		} else
			throw new IllegalArgumentException("Cannot byte-transform null object");

		return bytes;
	}

	private static void close(final Closeable stream) throws IOException {
		if (stream != null)
			stream.close();
	}

	private static Object convert(Class<?> objectType, String toConvert) {
		if (objectType.equals(String.class))
			return toConvert;
		if (objectType.equals(Byte.class) || objectType.equals(byte.class))
			return Byte.valueOf(toConvert);
		if (objectType.equals(Character.class) || objectType.equals(char.class)) {
			if (toConvert.length() == 1)
				return toConvert.charAt(0);
			else
				return '\u0000';
		}
		if (objectType.equals(Short.class) || objectType.equals(short.class))
			return Short.valueOf(toConvert);
		if (objectType.equals(Integer.class) || objectType.equals(int.class)) {
			if (toConvert == null)
				return 0;
			return Integer.valueOf(toConvert);
		}
		if (objectType.equals(Float.class) || objectType.equals(float.class)) {
			if (toConvert == null)
				return 0f;
			return Float.valueOf(toConvert);
		}
		if (objectType.equals(Double.class) || objectType.equals(double.class))
			return Double.valueOf(toConvert);
		if (objectType.equals(Long.class) || objectType.equals(long.class))
			return Long.valueOf(toConvert);
		if (objectType.equals(Boolean.class) || objectType.equals(boolean.class))
			return Boolean.valueOf(toConvert);
		if (objectType.equals(BigDecimal.class))
			return new BigDecimal(toConvert);
		if (objectType.equals(BigInteger.class))
			return new BigInteger(toConvert);
		return toConvert;
	}
}
