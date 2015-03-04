package de.codecentric.spa.sql;

import java.util.HashMap;

import de.codecentric.spa.metadata.EntityMetaData;
import de.codecentric.spa.metadata.EntityScanner;
import de.codecentric.spa.sql.SQLGenerator.SQLStatements;

/**
 * SQLProvider is singleton class containing basic SQL statements and strings
 * based on {@link EntityMetaData}s obtained through {@link EntityScanner}
 * scanning process.
 * 
 * @see EntityScanner#scanClass(Class)
 */
public final class SQLProvider {

	private static final SQLProvider INSTANCE = new SQLProvider();

	private HashMap<Class<?>, SQLStatements> sqlMap;

	/**
	 * Hidden constructor.
	 */
	private SQLProvider() {
		sqlMap = new HashMap<Class<?>, SQLGenerator.SQLStatements>(0);
	}

	/**
	 * Method adds generated {@link SQLStatements} object to this provider. This
	 * object can be latter retrieved via {@link SQLProvider#getSQL(Class)}
	 * method.
	 * 
	 * @param cls
	 *            class which SQL statements are passed in
	 * @param sql
	 *            {@link SQLStatements} to store in this provider
	 */
	public void addSQL(Class<?> cls, SQLStatements sql) {
		sqlMap.put(cls, sql);
	}

	/**
	 * Method returns {@link SQLStatements} related to given class.
	 * 
	 * @param cls
	 * @return {@link SQLStatements}
	 */
	public SQLStatements getSQL(Class<?> cls) {
		return sqlMap.get(cls);
	}

	/**
	 * Method returns a singleton instance of this class.
	 * 
	 * @return singleton instance of this class
	 */
	public static SQLProvider getInstance() {
		return INSTANCE;
	}

}
