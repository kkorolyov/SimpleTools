package dev.kkorolyov.simplespecs

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.concurrent.ThreadLocalRandom

/**
 * Provides utility methods for Specs.
 */
class SpecUtilities {
	/**
	 * Reflectively gets a field's value.
	 * @see #getField(java.lang.String, java.lang.Class, java.lang.Object)
	 */
	static Object getField(String field, Object object) {
		return getField(field, object.class, object)
	}
	/**
	 * Reflectively gets a static field's value.
	 * @see #getField(java.lang.String, java.lang.Class, java.lang.Object)
	 */
	static Object getField(String field, Class<?> c) {
		return getField(field, c, null)
	}
	/**
	 * Reflectively gets a field's value.
	 * @param field field name
	 * @param c declaring class
	 * @param object field owner
	 * @return {@code object's} value for {@code field} defined by class {@code c}
	 */
	static Object getField(String field, Class<?> c, Object object) {
		Field f = c.getDeclaredField(field)
		f.setAccessible(true)

		return f.get(object)
	}

	/**
	 * Reflectively sets a field's value
	 * @see #setField(java.lang.String, java.lang.Class, java.lang.Object, java.lang.Object)
	 */
	static void setField(String field, Object object, Object value) {
		setField(field, object.class, object, value)
	}
	/**
	 * Reflectively sets a static field's value.
	 * @see #setField(java.lang.String, java.lang.Class, java.lang.Object, java.lang.Object)
	 */
	static void setField(String field, Class<?> c, Object value) {
		setField(field, c, null, value)
	}
	/**
	 * Reflectively sets a field's value
	 * @param field field name
	 * @param c declaring class
	 * @param object field owner
	 * @param value new field value
	 */
	static void setField(String field, Class<?> c, Object object, Object value) {
		Field f = c.getDeclaredField(field)
		f.setAccessible(true)

		unfinalize(f).set(object, value)
	}
	private static Field unfinalize(Field f) {
		Field modifiers = Field.class.getDeclaredField("modifiers")
		modifiers.setAccessible(true)
		modifiers.setInt(f, f.modifiers & ~Modifier.FINAL)

		return f;
	}

	/** @return random int between {@code 0} and {@code bound} */
	static int randInt(int bound = Integer.MAX_VALUE) {
		return ThreadLocalRandom.current().nextInt(bound)
	}

	/** @return random byte between {@code 0} and {@code bound} */
	static byte randByte(byte bound = Byte.MAX_VALUE) {
		return ThreadLocalRandom.current().nextInt(bound)
	}
	/**
	 * @param length length of returned byte array
	 * @return byte array of length {@code length} populated with random bytes
	 */
	static byte[] randByteArray(int length) {
		return new byte[length].with {
			ThreadLocalRandom.current().nextBytes(it)
			return it
		}
	}

	/** @return random short between {@code 0} and {@code bound} */
	static short randShort(short bound = Short.MAX_VALUE) {
		return ThreadLocalRandom.current().nextInt(bound)
	}
	/** @return random long between {@code 0} and {@code bound} */
	static long randLong(long bound = Long.MAX_VALUE) {
		return ThreadLocalRandom.current().nextLong(bound)
	}

	/** @return random float between {@code 0} and {@code bound} */
	static float randFloat(float bound = Float.MAX_VALUE) {
		return ThreadLocalRandom.current().nextDouble(bound)
	}
	/** @return random double between {@code 0} and {@code bound} */
	static double randDouble(double bound = Double.MAX_VALUE) {
		return ThreadLocalRandom.current().nextDouble(bound)
	}

	/** @return random String generated using UUID */
	static String randString() {
		return UUID.randomUUID().toString().replaceAll("-", "")
	}
}
