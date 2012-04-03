package de.codecentric.voicenotes;

import de.codecentric.voicenotes.context.ApplicationContext;
import de.codecentric.voicenotes.context.DatabaseHelper;

/**
 * Persistence activity class - all activities that do some persistence jobs
 * should subclass this class.
 */
public abstract class PersistenceActivity extends BaseActivity {

	protected DatabaseHelper dbHelper;

	/**
	 * Opens the database connection.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		dbHelper = ((ApplicationContext) getApplication()).getDatabaseHelper();
	}

}
