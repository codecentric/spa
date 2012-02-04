package de.codecentric.spa.ctx;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import de.codecentric.spa.metadata.EntityMetaDataProvider;
import de.codecentric.spa.metadata.EntityScanner;
import de.codecentric.spa.sql.SQLGenerator;
import de.codecentric.spa.sql.SQLGenerator.SQLStatements;

/**
 * A helper class to manage database creation and version management.
 * 
 * Instances of this class must be created after all entity classes are scanned with {@link EntityScanner}.
 * 
 * Users should extend this class in order to implement needed logic in methods {@link #onCreate(SQLiteDatabase)} and
 * {@link #onUpgrade(SQLiteDatabase, int, int)}.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	protected List<SQLStatements> sqlStatements;

	/**
	 * Constructs database helper. Retrieves persistent classes from {@link EntityMetaDataProvider} used in given
	 * {@link PersistenceApplicationContext} and retrieves generated {@link SQLStatements} for each of those classes.
	 * 
	 * @param context
	 *            application context
	 * @param dbName
	 *            database name
	 * @param dbVersion
	 *            database version
	 */
	public DatabaseHelper(PersistenceApplicationContext context, String dbName, int dbVersion) {
		super(context, dbName, null, dbVersion);

		Class<?>[] persistentClasses = context.getEntityMetaDataProvider().getPersistentClasses();
		if (persistentClasses != null && persistentClasses.length != 0) {
			sqlStatements = new ArrayList<SQLGenerator.SQLStatements>(0);

			for (Class<?> cls : persistentClasses) {
				SQLStatements sql = context.getSQLProvider().getSQL(cls);
				if (sql != null) {
					sqlStatements.add(sql);
				}
			}
		}

	}

	/**
	 * Creation of whole database structure should happen here. Current implementation does nothing, it's up to user to
	 * define logic extending this class.
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		// Leave unimplemented - let users implement their logic.
	}

	/**
	 * Upgrading of whole database structure should happen here. Current implementation does nothing, it's up to user to
	 * define logic extending this class.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		// Leave unimplemented - let users implement their logic.
	}

	/**
	 * Compiles the SQL statement given as parameter.
	 * 
	 * @param statementSQL
	 *            SQL statement
	 * @return compiled SQL statement object
	 */
	public SQLiteStatement compileStatement(String statementSQL) {
		return getWritableDatabase().compileStatement(statementSQL);
	}

	/**
	 * Returns a database instance used by this application.
	 * 
	 * @return a database instance
	 */
	public SQLiteDatabase getDatabase() {
		return getWritableDatabase();
	}

}
