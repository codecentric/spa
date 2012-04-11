package de.codecentric.spa.sql;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;

/**
 * SQLiteTypeMapper class is used to map Java field types to appropriate SQLite
 * types.
 * 
 * NOTE: Primitive types are not supported.
 * 
 * This is how the types are mapped:
 * 
 * <ul>
 * <li>byte[] - TYPE_BLOB</li>
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
 * <li>java.util.Date - TYPE_INT</li>
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
		typePairs = new HashMap<String, String>();

		typePairs.put(Byte.class.getName(), TYPE_INT);
		typePairs.put(Short.class.getName(), TYPE_INT);
		typePairs.put(Integer.class.getName(), TYPE_INT);
		typePairs.put(Long.class.getName(), TYPE_INT);
		typePairs.put(Boolean.class.getName(), TYPE_INT);
		typePairs.put(Date.class.getName(), TYPE_INT);

		typePairs.put(Float.class.getName(), TYPE_REAL);
		typePairs.put(Double.class.getName(), TYPE_REAL);

		typePairs.put(String.class.getName(), TYPE_TEXT);
		typePairs.put(Character.class.getName(), TYPE_TEXT);

		typePairs.put(byte[].class.getName(), TYPE_BLOB);
	}

	/**
	 * Method reads the field's Java type and returns appropriate SQLite type.
	 * If the given field is of type that is not supported by the library, an
	 * {@link UnsupportedTypeException} is thrown.
	 * 
	 * @param f
	 * @return SQLite type that corresponds to the Java type of the input
	 * @throws if
	 *             type of the field is not supported, an
	 *             {@link UnsupportedTypeException} is raised
	 */
	public static String mapFieldType(Field f) throws UnsupportedTypeException {
		String type = typePairs.get(f.getType().getName());
		if (type == null) {
			throw new UnsupportedTypeException("Unsupported type mapped: " + f.getType().getName());
		}
		return type;
	}

}
