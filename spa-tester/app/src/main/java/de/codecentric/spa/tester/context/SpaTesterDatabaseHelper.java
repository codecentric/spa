package de.codecentric.spa.tester.context;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import de.codecentric.spa.ctx.DatabaseHelper;
import de.codecentric.spa.ctx.PersistenceContext;
import de.codecentric.spa.sql.SQLGenerator.SQLStatements;

public class SpaTesterDatabaseHelper extends DatabaseHelper {

	public SpaTesterDatabaseHelper(Context applicationContext, String dbName, int dbVersion) {
		super(applicationContext, dbName, dbVersion);
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
