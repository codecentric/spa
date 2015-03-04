package de.codecentric.spa.tester.context;

import android.app.Application;

import de.codecentric.spa.ctx.DatabaseHelper;
import de.codecentric.spa.ctx.PersistenceContext;

public class SpaTesterApplicationContext extends Application {

	private static int indentationLevel = 0;

    private DatabaseHelper dbHelper;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onCreate() {
		super.onCreate();

        String[] classNames = new String[] { "de.codecentric.spa.tester.entity.State", "de.codecentric.spa.tester.entity.City", "de.codecentric.spa.tester.entity.Government" };
        PersistenceContext persistenceContext = PersistenceContext.init(getApplicationContext(), classNames);

        // ... and then instantiate database helper
        dbHelper = new SpaTesterDatabaseHelper(getApplicationContext(), "VOICE_NOTES_DB", 1);
        dbHelper.retrieveSqlStatements(persistenceContext);
        persistenceContext.setDatabaseHelper(dbHelper);
	}

	public static void resetIndentationLevel() {
		indentationLevel = 0;
	}

	public static String increaseIndentationLevel() {
		String indentPrefix = "";
		indentationLevel++;
		for (int i = 0; i < indentationLevel; i++) {
			indentPrefix += "\t";
		}
		return indentPrefix;
	}

	public static String decreaseIndentationLevel() {
		String indentPrefix = "";
		indentationLevel--;
		if (indentationLevel < 0) {
			indentationLevel = 0;
		}
		for (int i = 0; i < indentationLevel; i++) {
			indentPrefix += "\t";
		}
		return indentPrefix;
	}

	public static String getIndentationPrefix() {
		String indentPrefix = "";
		for (int i = 0; i < indentationLevel; i++) {
			indentPrefix += "\t";
		}
		return indentPrefix;
	}

}
