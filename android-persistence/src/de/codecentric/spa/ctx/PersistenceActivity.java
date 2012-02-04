package de.codecentric.spa.ctx;

import android.app.Activity;

/**
 * Persistence activity class - all activities that do some persistence jobs should subclass this class.
 */
public abstract class PersistenceActivity extends Activity {

	protected DatabaseHelper dbHelper;

	/**
	 * Opens the database connection.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		dbHelper = ((PersistenceApplicationContext) getApplication()).getDatabaseHelper();

	}

}
