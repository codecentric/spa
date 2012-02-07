package de.codecentric.spa.sql;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * SQLiteTypeMapper class is used to map Java field types to appropriate SQLite
 * types.
 * 
 * This is how the types are mapped:
 * 
 * <ul>
 * <li>byte[] - TYPE_BLOB</li>
 * <li>byte - TYPE_INT</li>
 * <li>short - TYPE_INT</li>
 * <li>int - TYPE_INT</li>
 * <li>long - TYPE_INT</li>
 * <li>boolean - TYPE_INT</li>
 * <li>float - TYPE_REAL</li>
 * <li>double - TYPE_REAL</li>
 * <li>char - TYPE_TEXT</li>
 * <li>java.lang.String - TYPE_TEXT</li>
 * <li>java.lang.Byte - TYPE_INT</li>
 * <li>java.lang.Byte[] - NOT SUPPORTED</li>
 * <li>java.lang.Short - TYPE_INT</li>
 * <li>java.lang.Integer - TYPE_INT</li>
 * <li>java.lang.Long - TYPE_INT</li>
 * <li>java.lang.Float - TYPE_REAL</li>
 * <li>java.lang.Double - TYPE_REAL</li>
 * <li>java.lang.Boolean - TYPE_INT</li>
 * <li>java.lang.Character - TYPE_TEXT</li>
 * </ul>
 * 
 */
public class SQLiteTypeMapper {

	public static final String TYPE_TEXT = "TEXT";
	public static final String TYPE_INT = "INTEGER";
	public static final String TYPE_REAL = "REAL";
	public static final String TYPE_BLOB = "BLOB";
	public static final String TYPE_NULL = "NULL";

	private static HashMap<String, String> typePairs;

	static {
		typePairs = new HashMap<String, String>(0);
		typePairs.put(byte.class.getName(), TYPE_INT);
		typePairs.put(Byte.class.getName(), TYPE_INT);
		typePairs.put(short.class.getName(), TYPE_INT);
		typePairs.put(Short.class.getName(), TYPE_INT);
		typePairs.put(int.class.getName(), TYPE_INT);
		typePairs.put(Integer.class.getName(), TYPE_INT);
		typePairs.put(long.class.getName(), TYPE_INT);
		typePairs.put(Long.class.getName(), TYPE_INT);
		typePairs.put(boolean.class.getName(), TYPE_INT);
		typePairs.put(Boolean.class.getName(), TYPE_INT);

		typePairs.put(float.class.getName(), TYPE_REAL);
		typePairs.put(Float.class.getName(), TYPE_REAL);
		typePairs.put(double.class.getName(), TYPE_REAL);
		typePairs.put(Double.class.getName(), TYPE_REAL);

		typePairs.put(String.class.getName(), TYPE_TEXT);
		typePairs.put(char.class.getName(), TYPE_TEXT);
		typePairs.put(Character.class.getName(), TYPE_TEXT);

		typePairs.put(byte[].class.getName(), TYPE_BLOB);
	}

	/**
	 * Method reads the field's Java type and returns appropriate SQLite type or
	 * null if type could not be resolved.
	 * 
	 * @param f
	 * @return SQLite type that corresponds to the Java type of the input or
	 *         null, if type could not be resolved
	 */
	public static String mapFieldType(Field f) {
		return typePairs.get(f.getType().getName());
	}

}
