package de.codecentric.spa.tester.context;

import android.database.sqlite.SQLiteDatabase;
import de.codecentric.spa.ctx.DatabaseHelper;
import de.codecentric.spa.ctx.PersistenceApplicationContext;
import de.codecentric.spa.sql.SQLGenerator.SQLStatements;

public class SpaTesterDatabaseHelper extends DatabaseHelper {

	public SpaTesterDatabaseHelper(PersistenceApplicationContext context, String dbName, int dbVersion) {
		super(context, dbName, dbVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		if (sqlStatements != null && !sqlStatements.isEmpty()) {
			for (SQLStatements sql : sqlStatements) {
				database.execSQL(sql.getCreateTableSQL());
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		if (sqlStatements != null && !sqlStatements.isEmpty()) {
			for (SQLStatements sql : sqlStatements) {
				database.execSQL(sql.getDropTableSQL());
			}
		}
		onCreate(database);
	}

}
