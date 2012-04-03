package de.codecentric.voicenotes.context;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import de.codecentric.voicenotes.persistence.NoteEntityHelper;

/**
 * A helper class to manage database creation and version management.
 */
public final class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "voice_notes.db";
	private static final int DATABASE_VERSION = 1;

	private NoteEntityHelper textualNoteEntityHelper;

	/**
	 * Constructor. It also obtains the database instance (a writable one) which
	 * should remain open for entire life time of application.
	 * 
	 * @param context
	 */
	DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		textualNoteEntityHelper = new NoteEntityHelper();
	}

	/**
	 * Creation of whole database structure should happen here.
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(textualNoteEntityHelper.supplyCreateTableSQL());
	}

	/**
	 * Upgrading of whole database structure should happen here.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		database.execSQL(textualNoteEntityHelper.supplyDropTableSQL());
		onCreate(database);
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
